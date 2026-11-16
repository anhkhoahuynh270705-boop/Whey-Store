package com.gymstore.inventory.service.impl;

import com.gymstore.common.contract.ReserveItem;
import com.gymstore.common.contract.ReserveRequest;
import com.gymstore.common.contract.ReserveResult;
import com.gymstore.common.contract.StockActionRequest;
import com.gymstore.common.contract.StockActionResult;
import com.gymstore.common.exception.ConflictException;
import com.gymstore.common.exception.ResourceNotFoundException;
import com.gymstore.inventory.dto.request.CreateInventoryItemRequest;
import com.gymstore.inventory.dto.response.InventoryItemDto;
import com.gymstore.inventory.entity.InventoryItem;
import com.gymstore.inventory.entity.StockMovement;
import com.gymstore.inventory.entity.StockReservation;
import com.gymstore.inventory.enums.ReservationStatus;
import com.gymstore.inventory.enums.StockMovementType;
import com.gymstore.inventory.mapper.InventoryMapper;
import com.gymstore.inventory.repository.InventoryItemRepository;
import com.gymstore.inventory.repository.StockMovementRepository;
import com.gymstore.inventory.repository.StockReservationRepository;
import com.gymstore.inventory.service.IInventoryService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation của inventory service với distributed lock và optimistic
 * locking.
 */
@Service
public class InventoryServiceImpl implements IInventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private static final long LOCK_WAIT_SECONDS = 3;
    private static final long LOCK_LEASE_SECONDS = 10;
    private static final int OPTIMISTIC_RETRY = 3;

    private final InventoryItemRepository itemRepository;
    private final StockReservationRepository reservationRepository;
    private final StockMovementRepository movementRepository;
    private final RedissonClient redisson;
    private final TransactionTemplate txTemplate;
    private final InventoryMapper mapper;

    public InventoryServiceImpl(InventoryItemRepository itemRepository,
            StockReservationRepository reservationRepository,
            StockMovementRepository movementRepository,
            RedissonClient redisson,
            TransactionTemplate txTemplate,
            InventoryMapper mapper) {
        this.itemRepository = itemRepository;
        this.reservationRepository = reservationRepository;
        this.movementRepository = movementRepository;
        this.redisson = redisson;
        this.txTemplate = txTemplate;
        this.mapper = mapper;
    }

    @Override
    public List<InventoryItemDto> list() {
        return itemRepository.findAllByOrderByProductIdAsc().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public InventoryItemDto getByProductId(Long productId) {
        return itemRepository.findByProductId(productId)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Tồn kho", productId));
    }

    @Override
    public InventoryItemDto createItem(CreateInventoryItemRequest req) {
        if (itemRepository.existsByProductId(req.productId())) {
            throw new ConflictException("Đã tồn tại dòng kho cho sản phẩm " + req.productId());
        }
        InventoryItem item = mapper.toEntity(req);
        return mapper.toDto(itemRepository.save(item));
    }

    @Override
    public InventoryItemDto addStock(Long productId, int delta) {
        return executeWithLock(productId, () -> {
            InventoryItem item = itemRepository.findByProductId(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tồn kho", productId));
            item.setAvailableQty(item.getAvailableQty() + delta);
            return mapper.toDto(itemRepository.save(item));
        });
    }

    @Override
    public ReserveResult reserve(ReserveRequest request) {
        String orderId = request.orderId();
        List<ReserveItem> insufficient = new ArrayList<>();

        for (ReserveItem item : request.items()) {
            boolean ok = reserveOne(orderId, item);
            if (!ok) {
                insufficient.add(item);
            }
        }

        if (!insufficient.isEmpty()) {
            rollbackReserve(orderId);
            return new ReserveResult(false,
                    "Không đủ hàng cho " + insufficient.size() + " dòng",
                    insufficient);
        }
        log.info("Đặt giữ hàng thành công cho đơn {}", orderId);
        return new ReserveResult(true, "OK", List.of());
    }

    private boolean reserveOne(String orderId, ReserveItem item) {
        return executeWithLock(item.productId(), () -> {
            for (int attempt = 1; attempt <= OPTIMISTIC_RETRY; attempt++) {
                try {
                    return txTemplate.execute(status -> doReserve(orderId, item));
                } catch (OptimisticLockingFailureException e) {
                    log.warn("Optimistic lock conflict khi reserve {} (lần {})", item.productId(), attempt);
                    if (attempt == OPTIMISTIC_RETRY) {
                        throw new ConflictException("Xung đột tồn kho, vui lòng thử lại");
                    }
                }
            }
            return false;
        });
    }

    private Boolean doReserve(String orderId, ReserveItem item) {
        InventoryItem inv = itemRepository.findByProductId(item.productId())
                .orElseThrow(() -> new ConflictException("Chưa khai báo tồn kho cho sản phẩm " + item.productId()));
        if (inv.getAvailableQty() < item.quantity()) {
            log.debug("Không đủ hàng product {}: cần {}, còn {}", item.productId(), item.quantity(), inv.getAvailableQty());
            return false;
        }
        inv.setAvailableQty(inv.getAvailableQty() - item.quantity());
        inv.setReservedQty(inv.getReservedQty() + item.quantity());
        itemRepository.save(inv);

        StockReservation reservation = mapper.toReservation(orderId, item);
        reservationRepository.save(reservation);

        movementRepository.save(new StockMovement(item.productId(), orderId, item.quantity(), StockMovementType.RESERVE));
        return true;
    }

    private void rollbackReserve(String orderId) {
        List<StockReservation> reserved = reservationRepository.findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);
        for (StockReservation r : reserved) {
            executeWithLock(r.getProductId(), () -> {
                txTemplate.executeWithoutResult(status -> {
                    InventoryItem inv = itemRepository.findByProductId(r.getProductId())
                            .orElseThrow(() -> new ConflictException("Tồn kho thiếu cho " + r.getProductId()));
                    inv.setAvailableQty(inv.getAvailableQty() + r.getQuantity());
                    inv.setReservedQty(inv.getReservedQty() - r.getQuantity());
                    itemRepository.save(inv);
                    r.setStatus(ReservationStatus.RELEASED);
                    reservationRepository.save(r);
                    movementRepository.save(new StockMovement(r.getProductId(), orderId, r.getQuantity(), StockMovementType.RELEASE));
                });
                return null;
            });
        }
        log.info("Đã hoàn {} reservation của đơn {}", reserved.size(), orderId);
    }

    @Override
    public StockActionResult commit(StockActionRequest request) {
        String orderId = request.orderId();
        List<StockReservation> reserved = reservationRepository.findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);
        if (reserved.isEmpty()) {
            return new StockActionResult(true, "Không có reservation RESERVED (idempotent)");
        }
        for (StockReservation r : reserved) {
            executeWithLock(r.getProductId(), () -> {
                txTemplate.executeWithoutResult(status -> {
                    InventoryItem inv = itemRepository.findByProductId(r.getProductId())
                            .orElseThrow(() -> new ConflictException("Tồn kho thiếu cho " + r.getProductId()));
                    inv.setReservedQty(inv.getReservedQty() - r.getQuantity());
                    itemRepository.save(inv);
                    r.setStatus(ReservationStatus.COMMITTED);
                    reservationRepository.save(r);
                    movementRepository.save(new StockMovement(r.getProductId(), orderId, r.getQuantity(), StockMovementType.COMMIT));
                });
                return null;
            });
        }
        log.info("Commit kho thành công cho đơn {} ({} dòng)", orderId, reserved.size());
        return new StockActionResult(true, "Đã trừ kho " + reserved.size() + " dòng");
    }

    @Override
    public StockActionResult release(StockActionRequest request) {
        String orderId = request.orderId();
        List<StockReservation> reserved = reservationRepository.findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);
        if (reserved.isEmpty()) {
            return new StockActionResult(true, "Không có reservation RESERVED để hoàn (idempotent)");
        }
        for (StockReservation r : reserved) {
            executeWithLock(r.getProductId(), () -> {
                txTemplate.executeWithoutResult(status -> {
                    InventoryItem inv = itemRepository.findByProductId(r.getProductId())
                            .orElseThrow(() -> new ConflictException("Tồn kho thiếu cho " + r.getProductId()));
                    inv.setAvailableQty(inv.getAvailableQty() + r.getQuantity());
                    inv.setReservedQty(inv.getReservedQty() - r.getQuantity());
                    itemRepository.save(inv);
                    r.setStatus(ReservationStatus.RELEASED);
                    reservationRepository.save(r);
                    movementRepository.save(new StockMovement(r.getProductId(), orderId, r.getQuantity(), StockMovementType.RELEASE));
                });
                return null;
            });
        }
        log.info("Hoàn kho thành công cho đơn {} ({} dòng)", orderId, reserved.size());
        return new StockActionResult(true, "Đã hoàn " + reserved.size() + " dòng");
    }

    private <T> T executeWithLock(Long productId, java.util.function.Supplier<T> action) {
        RLock lock = redisson.getLock("inventory:lock:product:" + productId);
        boolean locked = false;
        try {
            locked = lock.tryLock(LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                throw new ConflictException("Không khoá được tồn kho cho sản phẩm " + productId + ", thử lại sau");
            }
            return action.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConflictException("Bị gián đoạn khi khoá tồn kho sản phẩm " + productId);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}

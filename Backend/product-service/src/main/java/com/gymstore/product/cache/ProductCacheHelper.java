package com.gymstore.product.cache;

import com.gymstore.product.dto.response.ProductDto;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class ProductCacheHelper {

    private static final Logger log = LoggerFactory.getLogger(ProductCacheHelper.class);
    private static final Duration TTL = Duration.ofMinutes(30);
    private static final Duration NULL_TTL = Duration.ofMinutes(2);

    private final RedisTemplate<String, Object> redis;
    private final RedissonClient redisson;

    public ProductCacheHelper(RedisTemplate<String, Object> redis, RedissonClient redisson) {
        this.redis = redis;
        this.redisson = redisson;
    }

    public ProductDto getOrLoad(Long id, Supplier<Optional<ProductDto>> loader) {
        String valueKey = "product:detail:" + id;
        String nullKey = "product:detail:null:" + id;

        // 1. Kiểm tra cache thường
        Object cached = redis.opsForValue().get(valueKey);
        if (cached instanceof ProductDto dto) {
            return dto;
        }
        // 2. Kiểm tra marker null (penetration)
        if (Boolean.TRUE.equals(redis.hasKey(nullKey))) {
            return null;
        }

        // 3. Stampede: chỉ 1 thread được rebuild
        RLock lock = redisson.getLock("product:cache:lock:" + id);
        boolean locked = false;
        try {
            locked = lock.tryLock(2, 10, TimeUnit.SECONDS); // wait 2s, lease 10s
            if (!locked) {
                log.debug("Không lấy được lock rebuild cho product {}, load trực tiếp", id);
                return loader.get().orElse(null);
            }
            // 4. Double-check sau khi có lock
            cached = redis.opsForValue().get(valueKey);
            if (cached instanceof ProductDto dto) {
                return dto;
            }
            if (Boolean.TRUE.equals(redis.hasKey(nullKey))) {
                return null;
            }
            // 5. Load thật
            Optional<ProductDto> loaded = loader.get();
            if (loaded.isPresent()) {
                redis.opsForValue().set(valueKey, loaded.get(), TTL);
                redis.delete(nullKey);
            } else {
                redis.opsForValue().set(nullKey, "1", NULL_TTL);
            }
            return loaded.orElse(null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return loader.get().orElse(null);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /** Xoá cache khi cập nhật/xoá sản phẩm. */
    public void evict(Long id) {
        redis.delete("product:detail:" + id);
        redis.delete("product:detail:null:" + id);
    }
}

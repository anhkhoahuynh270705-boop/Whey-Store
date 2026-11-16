import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StaffCustomersService } from '../customers.service';
import { Order, ShippingAddress } from '../../../core/models/order.model';
import { PageResponse } from '../../../core/models/product.model';

export interface CustomerSummary {
  userId: string;
  fullName: string;
  phone: string;
  address: string;
  orderCount: number;
  totalSpent: number;
  lastOrderAt: string;
}

@Component({
  selector: 'app-staff-customer-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './staff-customer-list.component.html',
  styleUrl: './staff-customer-list.component.scss',
})
export class StaffCustomerListComponent implements OnInit {
  customers: CustomerSummary[] = [];
  loading = true;

  private customersService = inject(StaffCustomersService);
  protected cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.customersService.getAllOrders(0, 100).subscribe({
      next: (res) => {
        const map = new Map<string, CustomerSummary>();

        for (const o of res.content) {
          let addr: ShippingAddress | null = null;
          try {
            addr = typeof o.shippingAddress === 'string' ? JSON.parse(o.shippingAddress) : o.shippingAddress;
          } catch {
            addr = null;
          }

          const key = addr?.phone || o.userId;
          const name = addr?.fullName || o.userId || 'Khách hàng';
          const fullAddress = addr ? [addr.addressLine, addr.ward, addr.district, addr.city].filter(Boolean).join(', ') : 'Chưa cập nhật';

          if (!map.has(key)) {
            map.set(key, {
              userId: o.userId,
              fullName: name,
              phone: addr?.phone || 'Chưa có',
              address: fullAddress,
              orderCount: 1,
              totalSpent: Number(o.totalAmount || 0),
              lastOrderAt: o.createdAt,
            });
          } else {
            const existing = map.get(key)!;
            existing.orderCount++;
            existing.totalSpent += Number(o.totalAmount || 0);
          }
        }

        this.customers = Array.from(map.values());
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  formatPrice(val: number): string {
    return new Intl.NumberFormat('vi-VN').format(val) + ' ₫';
  }

  formatDate(val: string): string {
    return val ? new Date(val).toLocaleDateString('vi-VN') : '';
  }
}

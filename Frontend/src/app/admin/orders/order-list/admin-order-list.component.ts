import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Order, PageResponse } from '../model/order.model';
import { AdminOrdersService } from '../service/orders.service';

@Component({
  selector: 'app-admin-order-list',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './admin-order-list.component.html',
  styleUrl: './admin-order-list.component.scss',
})
export class AdminOrderListComponent implements OnInit {
  data: PageResponse<Order> | null = null;
  loading = true;
  statusControl = new FormControl<string>('', { nonNullable: true });
  private ordersService = inject(AdminOrdersService);
  protected cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.ordersService.getAllOrders(0, 50).subscribe({
      next: (d) => {
        this.data = d;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

   get filtered(): Order[] {
    if (!this.data) return [];
    const status = this.statusControl.value;
    if (!status) return this.data.content;
    return this.data.content.filter((o) => o.status === status);
  }

  formatPrice(p: number): string {
    return new Intl.NumberFormat('vi-VN').format(p) + ' ₫';
  }
  formatDate(d: string): string {
    return new Date(d).toLocaleDateString('vi-VN');
  }
  statusClass(s: string): string {
    const m: Record<string, string> = {
      PENDING: 'badge-warning',
      RESERVED: 'badge-warning',
      PAID: 'badge-primary',
      COMPLETED: 'badge-success',
      CANCELLED: 'badge-error',
    };
    return 'badge ' + (m[s] ?? 'badge-muted');
  }

  statusOptions = ['', 'PENDING', 'RESERVED', 'PAID', 'COMPLETED', 'CANCELLED'];
}

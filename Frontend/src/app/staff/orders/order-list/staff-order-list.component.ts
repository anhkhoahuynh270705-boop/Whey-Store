import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { StaffOrdersService } from '../orders.service';
import { Order, PageResponse } from '../../../core/models/order.model';

@Component({
  selector: 'app-staff-order-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './staff-order-list.component.html',
  styleUrl: './staff-order-list.component.scss',
})
export class StaffOrderListComponent implements OnInit {
  data: PageResponse<Order> | null = null;
  loading = true;
  filterStatus = 'PENDING';
  private ordersService = inject(StaffOrdersService);
  protected cdr = inject(ChangeDetectorRef);

  statusOptions = ['', 'PENDING', 'RESERVED', 'PAID', 'COMPLETED', 'CANCELLED'];

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.ordersService.getAllOrders(0, 100).subscribe({
      next: d => {
        this.data = d;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  get filtered(): Order[] {
    if (!this.data) return [];
    if (!this.filterStatus) return this.data.content;
    return this.data.content.filter(o => o.status === this.filterStatus);
  }

  formatPrice(p: number): string { return new Intl.NumberFormat('vi-VN').format(p) + ' ₫'; }
  formatDate(d: string): string { return new Date(d).toLocaleDateString('vi-VN'); }
  statusClass(s: string): string {
    const m: Record<string,string> = { PENDING:'badge-warning', RESERVED:'badge-warning', PAID:'badge-primary', COMPLETED:'badge-success', CANCELLED:'badge-error' };
    return 'badge ' + (m[s] ?? 'badge-muted');
  }
}

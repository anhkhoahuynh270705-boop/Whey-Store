import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { OrdersService } from '../orders.service';
import { Order, PageResponse } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.scss',
})
export class OrderListComponent implements OnInit {
  data: PageResponse<Order> | null = null;
  loading = true;

  private ordersService = inject(OrdersService);
  protected cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.ordersService.getOrders().subscribe({
      next: (d) => {
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

  statusBadge(status: string): string {
    const map: Record<string, string> = {
      PENDING:   'badge badge-warning',
      RESERVED:  'badge badge-warning',
      PAID:      'badge badge-primary',
      COMPLETED: 'badge badge-success',
      CANCELLED: 'badge badge-error',
    };
    return map[status] ?? 'badge badge-muted';
  }

  statusIcon(status: string): string {
    const map: Record<string, string> = {
      PENDING:   'fas fa-clock',
      RESERVED:  'fas fa-hourglass-half',
      PAID:      'fas fa-check',
      COMPLETED: 'fas fa-check-double',
      CANCELLED: 'fas fa-times',
    };
    return map[status] ?? 'fas fa-question-circle';
  }

  formatPrice(p: number): string {
    return new Intl.NumberFormat('vi-VN').format(p) + ' ₫';
  }

  formatDate(d: string): string {
    return new Date(d).toLocaleDateString('vi-VN', {
      day: '2-digit', month: '2-digit', year: 'numeric'
    });
  }
}

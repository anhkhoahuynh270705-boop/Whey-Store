import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { OrdersService } from '../orders.service';
import { ToastService } from '../../../core/services/toast.service';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './order-detail.component.html',
  styleUrl: './order-detail.component.scss',
})
export class OrderDetailComponent implements OnInit {
  order: Order | null = null;
  loading = true;

  private route = inject(ActivatedRoute);
  private ordersService = inject(OrdersService);
  private toast = inject(ToastService);
  protected cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.ordersService.getOrder(id).subscribe({
      next: (o) => {
        this.order = o;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  downloadInvoice(): void {
    if (!this.order) return;
    this.ordersService.downloadInvoice(this.order.id).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `invoice-${this.order!.orderNumber}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
        this.toast.success('Đã tải hóa đơn thành công!');
        this.cdr.markForCheck();
      },
      error: () => {
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
    return new Date(d).toLocaleString('vi-VN');
  }
}

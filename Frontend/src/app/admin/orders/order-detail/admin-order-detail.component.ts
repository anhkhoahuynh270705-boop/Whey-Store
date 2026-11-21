import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Order } from '../model/order.model';
import { AdminOrdersService } from '../service/orders.service';
import { ToastService } from '../../../shared/toast/service/toast.service';

@Component({
  selector: 'app-admin-order-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-order-detail.component.html',
  styleUrl: './admin-order-detail.component.scss',
})
export class AdminOrderDetailComponent implements OnInit {
  order: Order | null = null;
  loading = true;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ordersService = inject(AdminOrdersService);
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
      },
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
      },
    });
  }

  statusClass(s: string): string {
    const m: Record<string, string> = {
      PENDING: 'badge badge-warning',
      RESERVED: 'badge badge-warning',
      PAID: 'badge badge-primary',
      COMPLETED: 'badge badge-success',
      CANCELLED: 'badge badge-error',
    };
    return m[s] ?? 'badge';
  }

  formatPrice(p: number): string {
    return new Intl.NumberFormat('vi-VN').format(p) + ' ₫';
  }

  formatDate(d: string): string {
    return new Date(d).toLocaleString('vi-VN');
  }

  goBack(): void {
    this.router.navigate(['/orders']);
  }
}

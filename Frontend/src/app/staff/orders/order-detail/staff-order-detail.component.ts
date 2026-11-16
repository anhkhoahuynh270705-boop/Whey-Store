import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StaffOrdersService } from '../orders.service';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-staff-order-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './staff-order-detail.component.html',
  styleUrl: './staff-order-detail.component.scss',
})
export class StaffOrderDetailComponent implements OnInit {
  order: Order | null = null;
  loading = true;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ordersService = inject(StaffOrdersService);
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

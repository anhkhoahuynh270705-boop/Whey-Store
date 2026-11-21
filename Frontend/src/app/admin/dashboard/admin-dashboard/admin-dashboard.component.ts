import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminDashboardService } from '../service/dashboard.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss',
})
export class AdminDashboardComponent implements OnInit {
  stats = { orders: 0, products: 0, inventory: 0, revenue: 0 };
  recentOrders: any[] = [];
  loading = true;
  private dashboardService = inject(AdminDashboardService);
  protected cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.dashboardService.getRecentOrders(0, 5).subscribe({
      next: (p) => {
        this.stats.orders = p.totalElements;
        this.recentOrders = p.content;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
    this.dashboardService.getProductCount().subscribe({
      next: (p) => {
        this.stats.products = p.totalElements;
        this.cdr.markForCheck();
      },
    });
    this.dashboardService.getInventory().subscribe({
      next: (inv) => {
        this.stats.inventory = inv.length;
        this.cdr.markForCheck();
      },
    });
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
      COMPLETED: 'badge-success',
      CANCELLED: 'badge-error',
      PAID: 'badge-primary',
    };
    return 'badge ' + (m[s] ?? 'badge-muted');
  }
}

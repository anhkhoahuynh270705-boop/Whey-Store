import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { StaffDashboardService } from './dashboard.service';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './staff-dashboard.component.html',
  styleUrl: './staff-dashboard.component.scss',
})
export class StaffDashboardComponent implements OnInit {
  stats = { pending: 0, reserved: 0, completed: 0, inventory: 0 };
  pendingOrders: any[] = [];
  loading = true;
  private dashboardService = inject(StaffDashboardService);
  protected cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.dashboardService.getAllOrders(0, 50).subscribe({
      next: (p) => {
        const orders = p.content;
        this.stats.pending   = orders.filter((o: any) => o.status === 'PENDING').length;
        this.stats.reserved  = orders.filter((o: any) => o.status === 'RESERVED').length;
        this.stats.completed = orders.filter((o: any) => o.status === 'COMPLETED').length;
        this.pendingOrders   = orders.filter((o: any) => ['PENDING','RESERVED'].includes(o.status)).slice(0, 8);
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
    this.dashboardService.getInventory().subscribe({
      next: (inv) => {
        this.stats.inventory = inv.length;
        this.cdr.markForCheck();
      }
    });
  }

  formatPrice(p: number): string { return new Intl.NumberFormat('vi-VN').format(p) + ' ₫'; }
  formatDate(d: string): string { return new Date(d).toLocaleDateString('vi-VN'); }
  statusClass(s: string): string {
    const m: Record<string,string> = { PENDING:'badge-warning', RESERVED:'badge-warning', PAID:'badge-primary', COMPLETED:'badge-success', CANCELLED:'badge-error' };
    return 'badge ' + (m[s] ?? 'badge-muted');
  }
}

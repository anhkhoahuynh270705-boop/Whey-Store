import { Component, ChangeDetectorRef, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { ToastComponent } from '../../shared/toast/toast.component';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, ToastComponent],
  templateUrl: './admin-layout.component.html',
  styleUrl: './admin-layout.component.scss',
})
export class AdminLayoutComponent {
  auth = inject(AuthService);
  protected cdr = inject(ChangeDetectorRef);
  sidebarOpen = signal(true);

  nav = [
    { label: 'Dashboard', icon: 'fa fa-chart-line', path: '/dashboard' },
    { label: 'Sản phẩm', icon: 'fa fa-box-open', path: '/products' },
    { label: 'Đơn hàng', icon: 'fa fa-shopping-cart', path: '/orders' },
    { label: 'Tồn kho', icon: 'fa fa-warehouse', path: '/inventory' },
    { label: 'Báo cáo', icon: 'fa fa-chart-bar', path: '/reports' },
    { label: 'Người dùng', icon: 'fa fa-users', path: '/users' },
  ];
}

import { Component, ChangeDetectorRef, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { ToastComponent } from '../../shared/toast/toast-modal/toast.component';

@Component({
  selector: 'app-staff-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, ToastComponent],
  templateUrl: './staff-layout.component.html',
  styleUrl: './staff-layout.component.scss',
})
export class StaffLayoutComponent {
  auth = inject(AuthService);
  protected cdr = inject(ChangeDetectorRef);
  sidebarOpen = signal(true);

  nav = [
    { label: 'Dashboard', icon: 'fa fa-tachometer-alt', path: '/dashboard' },
    { label: 'Đơn hàng', icon: 'fa fa-clipboard-list', path: '/orders' },
    { label: 'Tồn kho', icon: 'fa fa-boxes', path: '/inventory' },
    { label: 'Khách hàng', icon: 'fa fa-user', path: '/customers' },
  ];
}

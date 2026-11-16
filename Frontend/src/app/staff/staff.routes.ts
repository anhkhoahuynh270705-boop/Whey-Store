import { Routes } from '@angular/router';
import { roleGuard } from '../core/guards/role.guard';

export const staffRoutes: Routes = [
  {
    path: '',
    canActivate: [roleGuard('STAFF', 'ADMIN')],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./dashboard/staff-dashboard.component').then(m => m.StaffDashboardComponent),
      },
      {
        path: 'orders',
        loadComponent: () => import('./orders/order-list/staff-order-list.component').then(m => m.StaffOrderListComponent),
      },
      {
        path: 'orders/:id',
        loadComponent: () => import('./orders/order-detail/staff-order-detail.component').then(m => m.StaffOrderDetailComponent),
      },
      {
        path: 'inventory',
        loadComponent: () => import('./inventory/inventory-list/staff-inventory-list.component').then(m => m.StaffInventoryListComponent),
      },
      {
        path: 'customers',
        loadComponent: () => import('./customers/customer-list/staff-customer-list.component').then(m => m.StaffCustomerListComponent),
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },
  {
    path: 'unauthorized',
    loadComponent: () => import('../customer/unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent),
  },
  { path: '**', redirectTo: '/dashboard' },
];


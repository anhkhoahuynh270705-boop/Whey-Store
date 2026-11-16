import { Routes } from '@angular/router';
import { roleGuard } from '../core/guards/role.guard';

export const adminRoutes: Routes = [
  {
    path: '',
    canActivate: [roleGuard('ADMIN')],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./dashboard/admin-dashboard.component').then((m) => m.AdminDashboardComponent),
      },

      // Products CRUD
      {
        path: 'products',
        loadComponent: () =>
          import('./products/product-list/admin-product-list.component').then(
            (m) => m.AdminProductListComponent,
          ),
      },
      {
        path: 'products/create',
        loadComponent: () =>
          import('./products/product-create/admin-product-create.component').then(
            (m) => m.AdminProductCreateComponent,
          ),
      },
      {
        path: 'products/:id/edit',
        loadComponent: () =>
          import('./products/product-edit/admin-product-edit.component').then(
            (m) => m.AdminProductEditComponent,
          ),
      },
      {
        path: 'products/:id',
        loadComponent: () =>
          import('./products/product-detail/admin-product-detail.component').then(
            (m) => m.AdminProductDetailComponent,
          ),
      },

      // ── Orders
      {
        path: 'orders',
        loadComponent: () =>
          import('./orders/order-list/admin-order-list.component').then(
            (m) => m.AdminOrderListComponent,
          ),
      },
      {
        path: 'orders/:id',
        loadComponent: () =>
          import('./orders/order-detail/admin-order-detail.component').then(
            (m) => m.AdminOrderDetailComponent,
          ),
      },

      // ── Inventory
      {
        path: 'inventory',
        loadComponent: () =>
          import('./inventory/inventory-list/admin-inventory-list.component').then(
            (m) => m.AdminInventoryListComponent,
          ),
      },

      // ── Reports
      {
        path: 'reports',
        loadComponent: () =>
          import('./reports/admin-reports.component').then((m) => m.AdminReportsComponent),
      },

      // ── Users
      {
        path: 'users',
        loadComponent: () =>
          import('./users/admin-users.component').then((m) => m.AdminUsersComponent),
      },

      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },
  {
    path: 'unauthorized',
    loadComponent: () =>
      import('../customer/unauthorized/unauthorized.component').then(
        (m) => m.UnauthorizedComponent,
      ),
  },
  { path: '**', redirectTo: '/dashboard' },
];

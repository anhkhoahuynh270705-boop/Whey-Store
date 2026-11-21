import { Routes } from '@angular/router';
import { authGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: 'home',
    loadComponent: () =>
      import('./customer/home-modal/home/home.component').then((m) => m.HomeComponent),
  },
  {
    path: 'products',
    loadComponent: () =>
      import('./customer/products/product-list/product-list.component').then(
        (m) => m.ProductListComponent,
      ),
  },
  {
    path: 'products/:slug',
    loadComponent: () =>
      import('./customer/products/product-detail/product-detail.component').then(
        (m) => m.ProductDetailComponent,
      ),
  },
  {
    path: 'cart',
    loadComponent: () =>
      import('./customer/cart/cart-list/cart.component').then((m) => m.CartComponent),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./customer/auth/auth.component').then((m) => m.AuthComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./customer/auth/auth.component').then((m) => m.AuthComponent),
  },
  {
    path: 'checkout',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./customer/checkout/checkout-modal/checkout.component').then(
        (m) => m.CheckoutComponent,
      ),
  },
  {
    path: 'orders',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./customer/orders/order-list/order-list.component').then(
        (m) => m.OrderListComponent,
      ),
  },
  {
    path: 'orders/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./customer/orders/order-detail/order-detail.component').then(
        (m) => m.OrderDetailComponent,
      ),
  },
  {
    path: 'unauthorized',
    loadComponent: () =>
      import('./customer/unauthorized/unauthorized.component').then(
        (m) => m.UnauthorizedComponent,
      ),
  },
  { path: '**', redirectTo: 'home' },
];

import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { ToastComponent } from './shared/toast/toast-modal/toast.component';
import { ConfirmDialogComponent } from './shared/confirm/confirm-dialog/confirm-dialog.component';
import { AuthService } from './core/services/auth.service';
import { CartService } from './customer/cart/service/cart.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, ToastComponent, ConfirmDialogComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit {
  protected cdr = inject(ChangeDetectorRef);
  private authService = inject(AuthService);
  private cartService = inject(CartService);

  ngOnInit(): void {
    if (this.authService.isLoggedIn && this.cartService.guestId) {
      this.cartService.mergeOnLogin();
    } else {
      this.cartService.loadCart();
    }
  }
}

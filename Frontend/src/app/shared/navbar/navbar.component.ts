import { Component, ChangeDetectorRef, HostListener, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent {
  mobileOpen = false;
  scrolled = false;
  protected cdr = inject(ChangeDetectorRef);

  constructor(
    public auth: AuthService,
    public cart: CartService,
  ) {}

  get cartCount(): number {
    return this.cart.totalQuantity;
  }

  toggleMobile(): void {
    this.mobileOpen = !this.mobileOpen;
  }

  @HostListener('window:scroll')
  onScroll(): void {
    this.scrolled = window.scrollY > 20;
  }
}

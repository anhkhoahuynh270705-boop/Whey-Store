import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { ToastComponent } from './shared/toast/toast.component';
import { CartService } from './core/services/cart.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit {
  protected cdr = inject(ChangeDetectorRef);
  constructor(private cartService: CartService) {}
  ngOnInit(): void { this.cartService.loadCart(); }
}

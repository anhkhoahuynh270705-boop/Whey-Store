import { Component, ChangeDetectorRef, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CartService } from '../service/cart.service';
import { CartDto } from '../model/cart.model';
import { Observable } from 'rxjs';
import { ToastService } from '../../../shared/toast/service/toast.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss',
})
export class CartComponent {
  cart$: Observable<CartDto | null>;
  private toast = inject(ToastService);
  protected cdr = inject(ChangeDetectorRef);

  constructor(public cartService: CartService) {
    this.cart$ = cartService.cart$;
    cartService.loadCart();
  }

  update(productId: number, qty: number): void {
    this.cartService.updateItem(productId, qty);
    this.toast.info('Đã cập nhật số lượng sản phẩm');
  }

  remove(productId: number): void {
    this.cartService.removeItem(productId);
    this.toast.info('Đã xóa sản phẩm khỏi giỏ hàng');
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('vi-VN').format(price) + ' ₫';
  }

  effectivePrice(item: any): number {
    return item.salePrice ?? item.price;
  }
}

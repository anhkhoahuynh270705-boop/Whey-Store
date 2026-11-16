import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { CheckoutService } from './checkout.service';
import { CartService } from '../../core/services/cart.service';
import { ToastService } from '../../core/services/toast.service';
import { CartDto, CartItem } from '../../core/models/cart.model';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.scss',
})
export class CheckoutComponent implements OnInit {
  private fb              = inject(FormBuilder);
  private checkoutService = inject(CheckoutService);
  private cartService     = inject(CartService);
  private toast           = inject(ToastService);
  private router          = inject(Router);
  protected cdr           = inject(ChangeDetectorRef);

  cart$      = this.cartService.cart$;
  submitting = false;
  errorMsg   = '';

  paymentMethods = [
    { value: 'COD',          label: 'Thanh toán khi nhận hàng (COD)',          icon: 'fas fa-money-bill-wave' },
    { value: 'BANK_TRANSFER',label: 'Chuyển khoản ngân hàng',                  icon: 'fas fa-university' },
    { value: 'MOCK_GATEWAY', label: 'Cổng thanh toán trực tuyến (VNPAY / Thẻ)', icon: 'fas fa-credit-card' },
  ];

  form = this.fb.group({
    fullName:      ['', Validators.required],
    phone:         ['', [Validators.required, Validators.pattern(/^0\d{9}$/)]],
    addressLine:   ['', Validators.required],
    ward:          [''],
    district:      [''],
    city:          ['', Validators.required],
    note:          [''],
    paymentMethod: ['COD', Validators.required],
  });

  ngOnInit(): void { this.cartService.loadCart(); }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('vi-VN').format(price) + ' ₫';
  }

  effectivePrice(item: CartItem): number {
    return item.salePrice ?? item.price;
  }

  onSubmit(cart: CartDto): void {
    if (this.form.invalid || cart.items.length === 0) return;
    this.submitting = true;
    this.errorMsg = '';
    const v = this.form.value;

    const request = {
      cartId: cart.cartOwner,
      shippingAddress: {
        fullName:    v.fullName!,
        phone:       v.phone!,
        addressLine: v.addressLine!,
        ward:        v.ward!,
        district:    v.district!,
        city:        v.city!,
        note:        v.note!,
      },
      paymentMethod: v.paymentMethod!,
      items: cart.items.map((i) => ({
        productId:   i.productId,
        productName: i.name,
        quantity:    i.quantity,
        unitPrice:   this.effectivePrice(i),
      })),
    };

    this.checkoutService.checkout(request).subscribe({
      next: (resp) => {
        this.cartService.clearCart();
        this.toast.success('Đặt hàng thành công! Đang chuyển hướng...');
        this.cdr.markForCheck();
        this.router.navigate(['/orders'], {
          queryParams: { processId: resp.processInstanceId },
        });
      },
      error: (err) => {
        this.errorMsg = err.error?.message ?? 'Lỗi đặt hàng, vui lòng thử lại';
        this.submitting = false;
        this.cdr.markForCheck();
      },
    });
  }
}

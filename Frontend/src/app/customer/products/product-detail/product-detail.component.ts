import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductsService } from '../products.service';
import { CartService } from '../../../core/services/cart.service';
import { ToastService } from '../../../core/services/toast.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, DecimalPipe],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss',
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  loading = true;
  selectedImage = '';
  addedMsg = false;
  zoomed = false;

  addToCartForm: FormGroup;

  private route  = inject(ActivatedRoute);
  private productsService = inject(ProductsService);
  private cart   = inject(CartService);
  private toast  = inject(ToastService);
  private router = inject(Router);
  private fb     = inject(FormBuilder);
  protected cdr  = inject(ChangeDetectorRef);

  constructor() {
    this.addToCartForm = this.fb.group({
      quantity: [1, [Validators.required, Validators.min(1)]],
    });
  }

  ngOnInit(): void {
    const slug = this.route.snapshot.paramMap.get('slug')!;
    this.productsService.getProductBySlug(slug).subscribe({
      next: (p) => {
        this.product = p;
        this.selectedImage =
          p.images.find((i) => i.isPrimary)?.imageUrl || p.images[0]?.imageUrl || '';
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => this.router.navigate(['/products']),
    });
  }

  addToCart(): void {
    if (!this.product || this.addToCartForm.invalid) return;
    const quantity = this.addToCartForm.value.quantity;
    this.cart.addItem(this.product.id, quantity);
    this.toast.success(`Đã thêm ${quantity} sản phẩm vào giỏ hàng!`);
    this.addedMsg = true;
    this.cdr.markForCheck();
    setTimeout(() => {
      this.addedMsg = false;
      this.cdr.markForCheck();
    }, 2500);
  }

  incQty(): void {
    const q = this.addToCartForm.get('quantity')!;
    if (q.value < 99) q.setValue(q.value + 1);
  }

  decQty(): void {
    const q = this.addToCartForm.get('quantity')!;
    if (q.value > 1) q.setValue(q.value - 1);
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('vi-VN').format(price) + ' ₫';
  }

  get effectivePrice(): number {
    return this.product?.salePrice ?? this.product?.price ?? 0;
  }
}

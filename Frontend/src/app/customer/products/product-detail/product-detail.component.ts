import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Product } from '../model/product.model';
import { GalleryItem, WeightOption } from '../model/product.model';
import { ProductsService } from '../service/products.service';
import { CartService } from '../../cart/service/cart.service';
import { ToastService } from '../../../shared/toast/service/toast.service';

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
  zoomed = false;

  // Gallery images
  galleryImages: GalleryItem[] = [];

  // Variant selections
  selectedFlavor: string = '';
  selectedWeightOption: WeightOption | null = null;

  flavorOptions: string[] = [];
  weightOptions: WeightOption[] = [];

  addToCartForm: FormGroup;

  private route = inject(ActivatedRoute);
  private productsService = inject(ProductsService);
  private cart = inject(CartService);
  private toast = inject(ToastService);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  protected cdr = inject(ChangeDetectorRef);

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
        this.setupGallery(p);
        this.setupVariants(p);
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => this.router.navigate(['/products']),
    });
  }

  private setupGallery(p: Product): void {
    const baseImages: GalleryItem[] =
      p.images && p.images.length > 0
        ? p.images.map((img, idx) => ({
            id: img.id,
            imageUrl: img.imageUrl,
            label: idx === 0 ? 'Mặt trước' : `Góc chụp ${idx + 1}`,
          }))
        : [];

    const primaryUrl =
      p.images?.find((i) => i.isPrimary)?.imageUrl ||
      p.images?.[0]?.imageUrl ||
      'https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=600&auto=format&fit=crop&q=80';

    // Nếu chỉ có 1 ảnh, tạo thêm 3 góc chụp bổ sung (Dinh dưỡng, Góc nghiêng, Tem chống giả)
    if (baseImages.length === 0) {
      baseImages.push({ id: 1, imageUrl: primaryUrl, label: 'Mặt trước' });
    }

    if (baseImages.length < 4) {
      const extraAngles: { url: string; label: string }[] = [
        {
          url: 'https://images.unsplash.com/photo-1579722821273-0f6c7d44362f?w=600&auto=format&fit=crop&q=80',
          label: 'Góc nghiêng',
        },
        {
          url: 'https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?w=600&auto=format&fit=crop&q=80',
          label: 'Dinh dưỡng',
        },
        {
          url: 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600&auto=format&fit=crop&q=80',
          label: 'Tem chính hãng',
        },
      ];

      for (let i = baseImages.length; i < 4; i++) {
        const extra = extraAngles[i - 1] || extraAngles[0];
        baseImages.push({
          id: null,
          imageUrl: extra.url,
          label: extra.label,
        });
      }
    }

    this.galleryImages = baseImages;
    this.selectedImage = baseImages[0].imageUrl;
  }

  /**
   * Thiết lập các lựa chọn phân loại: Hương vị & Kích thước/Khối lượng
   */
  private setupVariants(p: Product): void {
    // 1. Hương vị
    const baseFlavor = p.flavor?.trim() || 'Socola (Chocolate)';
    if (baseFlavor.includes(',')) {
      this.flavorOptions = baseFlavor
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean);
    } else {
      this.flavorOptions = [
        baseFlavor,
        'Vani (Vanilla)',
        'Bánh quy (Cookies & Cream)',
        'Dâu tây (Strawberry)',
      ];
    }
    this.selectedFlavor = this.flavorOptions[0];

    // 2. Khối lượng & Số lần dùng & Tỉ lệ giá
    const baseWeight = p.weightGrams || 2270;
    const baseServings = p.servingSize || 75;

    if (baseWeight >= 2000) {
      this.weightOptions = [
        {
          grams: Math.round(baseWeight * 0.4),
          label: '2 Lbs (~900g)',
          servingSize: Math.round(baseServings * 0.4),
          priceMultiplier: 0.52,
        },
        {
          grams: baseWeight,
          label: `${(baseWeight / 1000).toLocaleString('vi-VN', { maximumFractionDigits: 1 })} kg (Chuẩn)`,
          servingSize: baseServings,
          priceMultiplier: 1.0,
        },
        {
          grams: Math.round(baseWeight * 2),
          label: '10 Lbs (~4.5kg - Siêu tiết kiệm)',
          servingSize: Math.round(baseServings * 2),
          priceMultiplier: 1.85,
        },
      ];
    } else {
      this.weightOptions = [
        {
          grams: baseWeight,
          label: `${baseWeight}g (Hộp nhỏ)`,
          servingSize: baseServings,
          priceMultiplier: 1.0,
        },
        {
          grams: baseWeight * 2,
          label: `${((baseWeight * 2) / 1000).toLocaleString('vi-VN', { maximumFractionDigits: 1 })} kg (Hộp lớn)`,
          servingSize: baseServings * 2,
          priceMultiplier: 1.8,
        },
      ];
    }

    this.selectedWeightOption =
      this.weightOptions.length > 1 ? this.weightOptions[1] : this.weightOptions[0];
  }

  selectImage(img: GalleryItem): void {
    this.selectedImage = img.imageUrl;
    this.zoomed = false;
    this.cdr.markForCheck();
  }

  selectFlavor(f: string): void {
    this.selectedFlavor = f;
    this.cdr.markForCheck();
  }

  selectWeightOption(w: WeightOption): void {
    this.selectedWeightOption = w;
    this.cdr.markForCheck();
  }

  addToCart(): void {
    if (!this.product || this.addToCartForm.invalid) return;
    const quantity = this.addToCartForm.value.quantity;
    this.cart.addItem(this.product.id, quantity);
    const weightLabel = this.selectedWeightOption ? `[${this.selectedWeightOption.label}]` : '';
    const flavorLabel = this.selectedFlavor ? `[Vị ${this.selectedFlavor}]` : '';
    this.toast.success(
      `Đã thêm ${quantity}x ${this.product.name} ${flavorLabel} ${weightLabel} vào giỏ hàng!`,
    );
  }

  buyNow(): void {
    if (!this.product || this.addToCartForm.invalid) return;
    this.addToCart();
    this.router.navigate(['/cart']);
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
    return new Intl.NumberFormat('vi-VN').format(Math.round(price)) + ' ₫';
  }

  get currentPrice(): number {
    const mult = this.selectedWeightOption?.priceMultiplier ?? 1.0;
    const base = this.product?.salePrice ?? this.product?.price ?? 0;
    return Math.round(base * mult);
  }

  get currentOrigPrice(): number {
    const mult = this.selectedWeightOption?.priceMultiplier ?? 1.0;
    return Math.round((this.product?.price ?? 0) * mult);
  }

  get discountPercent(): number {
    if (!this.product?.salePrice || this.product.salePrice >= this.product.price) return 0;
    return Math.round((1 - this.product.salePrice / this.product.price) * 100);
  }
}

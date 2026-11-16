import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminProductsService } from '../products.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-admin-product-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-product-detail.component.html',
  styleUrl: './admin-product-detail.component.scss',
})
export class AdminProductDetailComponent implements OnInit {
  product: Product | null = null;
  loading = true;
  errorMsg = '';
  selectedImage = '';

  private productsService = inject(AdminProductsService);
  private route  = inject(ActivatedRoute);
  private router = inject(Router);
  protected cdr  = inject(ChangeDetectorRef);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productsService.getProductById(id).subscribe({
      next: (p: Product) => {
        this.product = p;
        this.selectedImage =
          p.images?.find(i => i.isPrimary)?.imageUrl ?? p.images?.[0]?.imageUrl ?? '';
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMsg = 'Không tìm thấy sản phẩm';
        this.loading  = false;
        this.cdr.markForCheck();
      }
    });
  }

  selectImage(url: string): void {
    this.selectedImage = url;
    this.cdr.markForCheck();
  }

  goEdit(): void   { this.router.navigate(['/products', this.product!.id, 'edit']); }
  goList(): void   { this.router.navigate(['/products']); }

  deleteProduct(): void {
    if (!this.product) return;
    if (!confirm('Xác nhận xoá sản phẩm? Hành động này không thể hoàn tác.')) return;
    this.productsService.deleteProduct(this.product.id).subscribe({
      next: () => this.router.navigate(['/products']),
      error: () => { alert('Xoá thất bại'); }
    });
  }

  formatPrice(p: number): string {
    return new Intl.NumberFormat('vi-VN').format(p) + ' ₫';
  }

  formatDate(d: string): string {
    return new Date(d).toLocaleString('vi-VN');
  }
}


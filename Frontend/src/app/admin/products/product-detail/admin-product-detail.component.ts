import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Product } from '../model/product.model';
import { AdminProductsService } from '../service/products.service';
import { ConfirmDialogService } from '../../../shared/confirm/service/confirm-dialog.service';
import { ToastService } from '../../../shared/toast/service/toast.service';

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
  selectedImage = '';

  private productsService = inject(AdminProductsService);
  private toast = inject(ToastService);
  private confirm = inject(ConfirmDialogService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  protected cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productsService.getProductById(id).subscribe({
      next: (p: Product) => {
        this.product = p;
        this.selectedImage =
          p.images?.find((i) => i.isPrimary)?.imageUrl ?? p.images?.[0]?.imageUrl ?? '';
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.toast.error('Không tìm thấy thông tin sản phẩm');
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  selectImage(url: string): void {
    this.selectedImage = url;
    this.cdr.markForCheck();
  }

  goEdit(): void {
    this.router.navigate(['/products', this.product!.id, 'edit']);
  }
  goList(): void {
    this.router.navigate(['/products']);
  }

  async deleteProduct(): Promise<void> {
    if (!this.product) return;
    const ok = await this.confirm.open({
      title: 'Xoá sản phẩm?',
      message: `"${this.product.name}" sẽ bị xoá vĩnh viễn. Hành động này không thể hoàn tác.`,
      confirmLabel: 'Xoá',
      cancelLabel: 'Giữ lại',
      variant: 'danger',
    });
    if (!ok) return;
    this.productsService.deleteProduct(this.product.id).subscribe({
      next: () => this.router.navigate(['/products']),
      error: () => {
        alert('Xoá thất bại');
      },
    });
  }

  formatPrice(p: number): string {
    return new Intl.NumberFormat('vi-VN').format(p) + ' ₫';
  }

  formatDate(d: string): string {
    return new Date(d).toLocaleString('vi-VN');
  }
}

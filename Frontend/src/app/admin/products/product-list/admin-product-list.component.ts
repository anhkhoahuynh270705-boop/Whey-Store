import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminProductsService } from '../products.service';
import { ToastService } from '../../../core/services/toast.service';
import { PageResponse, ProductListItem } from '../../../core/models/product.model';

@Component({
  selector: 'app-admin-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-product-list.component.html',
  styleUrl: './admin-product-list.component.scss',
})
export class AdminProductListComponent implements OnInit {
  data: PageResponse<ProductListItem> | null = null;
  loading = true;
  search = '';
  togglingId: number | null = null;

  private productsService = inject(AdminProductsService);
  private toast = inject(ToastService);
  private router = inject(Router);
  protected cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.productsService
      .getProducts({ page: 0, size: 100, includeInactive: true } as any)
      .subscribe({
        next: (d) => {
          this.data = d;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  goCreate(): void {
    this.router.navigate(['/products/create']);
  }
  goEdit(id: number): void {
    this.router.navigate(['/products', id, 'edit']);
  }
  goDetail(id: number): void {
    this.router.navigate(['/products', id]);
  }

  toggleStatus(p: ProductListItem): void {
    this.togglingId = p.id;
    const newStatus = p.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    this.productsService.updateProduct(p.id, { status: newStatus } as any).subscribe({
      next: () => {
        this.togglingId = null;
        this.toast.success(`Đã chuyển sản phẩm sang trạng thái ${newStatus === 'ACTIVE' ? 'Kinh doanh' : 'Ngừng bán'}`);
        this.load();
        this.cdr.markForCheck();
      },
      error: () => {
        this.togglingId = null;
        this.cdr.markForCheck();
      },
    });
  }

  deleteProduct(id: number): void {
    if (!confirm('Xác nhận xoá sản phẩm? Hành động này không thể hoàn tác.')) return;
    this.productsService.deleteProduct(id).subscribe({
      next: () => {
        this.toast.success('Đã xoá sản phẩm thành công!');
        this.load();
        this.cdr.markForCheck();
      },
      error: () => {
        this.cdr.markForCheck();
      },
    });
  }

  formatPrice(p: number): string {
    return new Intl.NumberFormat('vi-VN').format(p) + ' ₫';
  }

  get filtered(): ProductListItem[] {
    if (!this.data) return [];
    const q = this.search.toLowerCase();
    return this.data.content.filter(
      (p) => !q || p.name.toLowerCase().includes(q) || p.brandName.toLowerCase().includes(q),
    );
  }
}

import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ConfirmDialogService } from '../../../shared/confirm/service/confirm-dialog.service';
import { PageResponse, ProductListItem } from '../model/product.model';
import { Subscription, filter } from 'rxjs';
import { AdminProductsService } from '../service/products.service';
import { ToastService } from '../../../shared/toast/service/toast.service';

@Component({
  selector: 'app-admin-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-product-list.component.html',
  styleUrl: './admin-product-list.component.scss',
})
export class AdminProductListComponent implements OnInit, OnDestroy {
  data: PageResponse<ProductListItem> | null = null;
  loading = true;
  search = '';
  togglingId: number | null = null;

  private productsService = inject(AdminProductsService);
  private toast = inject(ToastService);
  private confirm = inject(ConfirmDialogService);
  private router = inject(Router);
  protected cdr = inject(ChangeDetectorRef);

  private routerSub?: Subscription;

  ngOnInit(): void {
    this.load();
    this.routerSub = this.router.events
      .pipe(filter((e) => e instanceof NavigationEnd))
      .subscribe((e: NavigationEnd) => {
        if (e.urlAfterRedirects === '/products' || e.urlAfterRedirects === '/products/') {
          this.load();
        }
      });
  }

  ngOnDestroy(): void {
    this.routerSub?.unsubscribe();
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
        this.toast.success(`Đã chuyển sang ${newStatus === 'ACTIVE' ? 'Kinh doanh' : 'Ngừng bán'}`);
        this.load();
        this.cdr.markForCheck();
      },
      error: () => {
        this.togglingId = null;
        this.cdr.markForCheck();
      },
    });
  }

  async deleteProduct(id: number): Promise<void> {
    const ok = await this.confirm.open({
      title: 'Xoá sản phẩm?',
      message:
        'Hành động này không thể hoàn tác. Toàn bộ ảnh và dữ liệu liên quan sẽ bị xoá vĩnh viễn.',
      confirmLabel: 'Xoá',
      cancelLabel: 'Giữ lại',
      variant: 'danger',
    });
    if (!ok) return;

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

import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Brand, Category, Product } from '../model/product.model';
import { ImgCountPipe } from '../helper/img-count.pipe';
import { AdminProductsService } from '../service/products.service';
import { ToastService } from '../../../shared/toast/service/toast.service';

@Component({
  selector: 'app-admin-product-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ImgCountPipe],
  templateUrl: './admin-product-edit.component.html',
  styleUrl: './admin-product-edit.component.scss',
})
export class AdminProductEditComponent implements OnInit {
  form!: FormGroup;
  product: Product | null = null;
  categories: Category[] = [];
  brands: Brand[] = [];
  initLoading = true;
  saving = false;
  imageSlots: ({ id: number | null; url: string } | null)[] = [null, null, null, null];

  private productsService = inject(AdminProductsService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  protected cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.buildForm();
    const id = Number(this.route.snapshot.paramMap.get('id'));

    let pending = 3;
    const done = () => {
      if (--pending === 0) {
        this.initLoading = false;
        this.cdr.markForCheck();
      }
    };

    this.productsService.getCategories().subscribe({
      next: (c) => {
        this.categories = c;
        done();
      },
      error: done,
    });
    this.productsService.getBrands().subscribe({
      next: (b) => {
        this.brands = b;
        done();
      },
      error: done,
    });

    this.productsService.getProductById(id).subscribe({
      next: (p: Product) => {
        this.product = p;
        this.form.patchValue({
          name: p.name,
          description: p.description,
          categoryId: p.categoryId != null ? Number(p.categoryId) : null,
          brandId: p.brandId != null ? Number(p.brandId) : null,
          price: p.price,
          salePrice: p.salePrice,
          weightGrams: p.weightGrams,
          flavor: p.flavor,
          servingSize: p.servingSize,
          status: p.status,
        });

        // Điền ảnh vào slots theo thứ tự, primary đứng đầu
        const sorted = [...(p.images ?? [])].sort((a, b) =>
          a.isPrimary === b.isPrimary ? 0 : a.isPrimary ? -1 : 1,
        );
        sorted.slice(0, 4).forEach((img, i) => {
          this.imageSlots[i] = { id: img.id, url: img.imageUrl };
        });

        done();
      },
      error: () => {
        this.toast.error('Không tải được thông tin sản phẩm');
        done();
      },
    });
  }

  private buildForm(): void {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: [''],
      categoryId: [null, Validators.required],
      brandId: [null, Validators.required],
      price: [null, [Validators.required, Validators.min(0)]],
      salePrice: [null],
      weightGrams: [null],
      flavor: [''],
      servingSize: [null],
      status: ['ACTIVE', Validators.required],
    });
  }

  f(name: string) {
    return this.form.get(name);
  }

  // ── Image management ────────────────────────────────

  triggerFilePick(index: number): void {
    const input = document.getElementById(`img-input-${index}`) as HTMLInputElement;
    input?.click();
  }

  onFilePicked(event: Event, index: number): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) {
      this.toast.error('Chỉ chấp nhận file ảnh');
      return;
    }
    const reader = new FileReader();
    reader.onload = () => {
      // Giữ lại id cũ nếu đang replace ảnh đã có
      const existingId = this.imageSlots[index]?.id ?? null;
      this.imageSlots[index] = { id: existingId, url: reader.result as string };
      this.cdr.markForCheck();
    };
    reader.readAsDataURL(file);
  }

  removeImage(index: number, event: MouseEvent): void {
    event.stopPropagation();
    this.imageSlots[index] = null;
    this.cdr.markForCheck();
  }

  // ── Submit ──────────────────────────────────────────

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (!this.product) return;
    this.saving = true;
    const v = this.form.value;

    const images = this.imageSlots
      .map((slot, i) => (slot ? { id: slot.id, imageUrl: slot.url, isPrimary: i === 0 } : null))
      .filter(Boolean) as { id: number | null; imageUrl: string; isPrimary: boolean }[];

    const payload = {
      name: v.name,
      slug: this.product.slug ?? '',
      description: v.description || null,
      categoryId: Number(v.categoryId),
      brandId: Number(v.brandId),
      price: Number(v.price),
      salePrice: v.salePrice ? Number(v.salePrice) : null,
      weightGrams: v.weightGrams ? Number(v.weightGrams) : null,
      flavor: v.flavor || null,
      servingSize: v.servingSize ? Number(v.servingSize) : null,
      status: v.status,
      images,
    };

    this.productsService.updateProduct(this.product.id, payload).subscribe({
      next: () => {
        this.saving = false;
        this.toast.success('Cập nhật sản phẩm thành công!');
        this.cdr.markForCheck();
        setTimeout(() => this.router.navigate(['/products', this.product!.id]), 1000);
      },
      error: (err: any) => {
        this.saving = false;
        this.toast.error(err?.error?.message ?? 'Lỗi khi lưu');
        this.cdr.markForCheck();
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/products', this.product?.id ?? '']);
  }
  goList(): void {
    this.router.navigate(['/products']);
  }
}

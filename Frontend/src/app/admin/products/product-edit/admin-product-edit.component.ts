import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminProductsService } from '../products.service';
import { ToastService } from '../../../core/services/toast.service';
import { Brand, Category, Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-admin-product-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
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
  errorMsg = '';
  successMsg = '';

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
          imageUrl: p.images?.find((i) => i.isPrimary)?.imageUrl ?? p.images?.[0]?.imageUrl ?? '',
        });
        done();
      },
      error: () => {
        this.errorMsg = 'Không tải được sản phẩm';
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
      imageUrl: [''],
    });
  }

  f(name: string) {
    return this.form.get(name);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (!this.product) return;
    this.saving = true;
    this.errorMsg = '';
    const v = this.form.value;

    const payload = {
      name: v.name,
      slug: this.product?.slug ?? '',
      description: v.description || null,
      categoryId: Number(v.categoryId),
      brandId: Number(v.brandId),
      price: Number(v.price),
      salePrice: v.salePrice ? Number(v.salePrice) : null,
      weightGrams: v.weightGrams ? Number(v.weightGrams) : null,
      flavor: v.flavor || null,
      servingSize: v.servingSize ? Number(v.servingSize) : null,
      status: v.status,
      images: v.imageUrl
        ? [{ id: this.product?.images?.find((i) => i.isPrimary)?.id ?? null, imageUrl: v.imageUrl, isPrimary: true }]
        : (this.product?.images ?? []),
    };

    this.productsService.updateProduct(this.product.id, payload).subscribe({
      next: () => {
        this.saving = false;
        this.successMsg = 'Lưu thành công!';
        this.toast.success('Cập nhật sản phẩm thành công!');
        this.cdr.markForCheck();
        setTimeout(() => this.router.navigate(['/products', this.product!.id]), 1000);
      },
      error: (err: any) => {
        this.saving = false;
        this.errorMsg = err?.error?.message ?? 'Lỗi khi lưu, vui lòng thử lại';
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

import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminProductsService } from '../products.service';
import { ToastService } from '../../../core/services/toast.service';
import { Brand, Category } from '../../../core/models/product.model';

@Component({
  selector: 'app-admin-product-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-product-create.component.html',
  styleUrl: './admin-product-create.component.scss',
})
export class AdminProductCreateComponent implements OnInit {
  form!: FormGroup;
  categories: Category[] = [];
  brands: Brand[] = [];
  loading = false;
  initLoading = true;
  errorMsg = '';
  successMsg = '';

  private productsService = inject(AdminProductsService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  protected cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.buildForm();
    let pending = 2;
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
    this.loading = true;
    this.errorMsg = '';
    const v = this.form.value;

    const payload = {
      name: v.name,
      slug: '',
      description: v.description || null,
      categoryId: Number(v.categoryId),
      brandId: Number(v.brandId),
      price: Number(v.price),
      salePrice: v.salePrice ? Number(v.salePrice) : null,
      weightGrams: v.weightGrams ? Number(v.weightGrams) : null,
      flavor: v.flavor || null,
      servingSize: v.servingSize ? Number(v.servingSize) : null,
      status: v.status,
      images: v.imageUrl ? [{ id: null, imageUrl: v.imageUrl, isPrimary: true }] : [],
    };

    this.productsService.createProduct(payload).subscribe({
      next: (product) => {
        this.loading = false;
        this.successMsg = 'Tạo sản phẩm thành công!';
        this.toast.success('Tạo sản phẩm thành công!');
        this.cdr.markForCheck();
        setTimeout(() => this.router.navigate(['/products', product.id]), 1000);
      },
      error: (err: any) => {
        this.loading = false;
        this.errorMsg = err?.error?.message ?? 'Lỗi khi tạo sản phẩm, vui lòng thử lại';
        this.cdr.markForCheck();
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/products']);
  }
}

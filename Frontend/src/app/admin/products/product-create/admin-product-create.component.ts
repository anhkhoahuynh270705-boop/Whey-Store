import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Brand, Category } from '../model/product.model';
import { ImgCountPipe } from '../helper/img-count.pipe';
import { AdminProductsService } from '../service/products.service';
import { AdminInventoryService } from '../../inventory/service/inventory.service';
import { ToastService } from '../../../shared/toast/service/toast.service';

@Component({
  selector: 'app-admin-product-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ImgCountPipe],
  templateUrl: './admin-product-create.component.html',
  styleUrl: './admin-product-create.component.scss',
})
export class AdminProductCreateComponent implements OnInit {
  form!: FormGroup;
  categories: Category[] = [];
  brands: Brand[] = [];
  loading = false;
  initLoading = true;

  imageSlots: (string | null)[] = [null, null, null, null];

  private productsService = inject(AdminProductsService);
  private inventoryService = inject(AdminInventoryService);
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
      status: ['ACTIVE', Validators.required],
      variants: this.fb.array([this.newVariant()]),
    });
  }

  // ── Variants ──────────────────────────────────────────

  get variants(): FormArray {
    return this.form.get('variants') as FormArray;
  }

  newVariant(): FormGroup {
    return this.fb.group({
      flavor: ['', Validators.required],
      weightGrams: [null, [Validators.required, Validators.min(1)]],
      servingSize: [null],
      sku: ['', Validators.required],
      stock: [0, [Validators.required, Validators.min(0)]],
    });
  }

  addVariant(): void {
    this.variants.push(this.newVariant());
  }

  removeVariant(i: number): void {
    if (this.variants.length > 1) this.variants.removeAt(i);
    else this.toast.warning('Sản phẩm cần ít nhất 1 hương vị / kích thước');
  }

  vf(i: number, field: string) {
    return this.variants.at(i).get(field);
  }

  // ── Images ────────────────────────────────────────────

  triggerFilePick(index: number): void {
    (document.getElementById(`img-input-${index}`) as HTMLInputElement)?.click();
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
      this.imageSlots[index] = reader.result as string;
      this.cdr.markForCheck();
    };
    reader.readAsDataURL(file);
  }

  removeImage(index: number, event: MouseEvent): void {
    event.stopPropagation();
    this.imageSlots[index] = null;
    this.cdr.markForCheck();
  }

  f(name: string) {
    return this.form.get(name);
  }

  // ── Submit ────────────────────────────────────────────

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    const v = this.form.value;

    // Dùng variant đầu tiên làm giá trị mặc định cho product-level fields
    const firstVariant = v.variants[0];

    const images = this.imageSlots
      .map((url, i) => (url ? { id: null, imageUrl: url, isPrimary: i === 0 } : null))
      .filter(Boolean) as { id: null; imageUrl: string; isPrimary: boolean }[];

    const productPayload = {
      name: v.name,
      slug: '',
      description: v.description || null,
      categoryId: Number(v.categoryId),
      brandId: Number(v.brandId),
      price: Number(v.price),
      salePrice: v.salePrice ? Number(v.salePrice) : null,
      weightGrams: firstVariant.weightGrams ? Number(firstVariant.weightGrams) : null,
      flavor: firstVariant.flavor || null,
      servingSize: firstVariant.servingSize ? Number(firstVariant.servingSize) : null,
      status: v.status,
      images,
    };

    this.productsService.createProduct(productPayload).subscribe({
      next: (product) => {
        // Tạo inventory item cho từng variant song song
        const inventoryRequests = v.variants.map((variant: any) =>
          this.inventoryService.createItem(product.id, variant.sku.trim(), Number(variant.stock)),
        );

        forkJoin(inventoryRequests).subscribe({
          next: () => {
            this.loading = false;
            this.toast.success('Tạo sản phẩm và cập nhật kho thành công!');
            this.cdr.markForCheck();
            setTimeout(() => this.router.navigate(['/products', product.id]), 800);
          },
          error: () => {
            this.loading = false;
            this.toast.warning(
              'Sản phẩm đã tạo nhưng có lỗi khi tạo kho — vui lòng kiểm tra lại tồn kho',
            );
            this.cdr.markForCheck();
            setTimeout(() => this.router.navigate(['/products', product.id]), 1200);
          },
        });
      },
      error: (err: any) => {
        this.loading = false;
        this.toast.error(err?.error?.message ?? 'Lỗi khi tạo sản phẩm');
        this.cdr.markForCheck();
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/products']);
  }
}

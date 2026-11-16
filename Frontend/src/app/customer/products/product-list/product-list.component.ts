import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { ProductsService } from '../products.service';
import { CartService } from '../../../core/services/cart.service';
import { ToastService } from '../../../core/services/toast.service';
import {
  Brand, Category, PageResponse, ProductFilter, ProductListItem,
} from '../../../core/models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [RouterLink, CommonModule, ReactiveFormsModule],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss',
})
export class ProductListComponent implements OnInit {
  products: PageResponse<ProductListItem> | null = null;
  categories: Category[] = [];
  brands: Brand[] = [];
  loading = false;
  currentPage = 0;
  
  filterForm: FormGroup;

  sortOptions = [
    { label: 'Mới nhất', value: 'id,desc' },
    { label: 'Giá tăng dần', value: 'price,asc' },
    { label: 'Giá giảm dần', value: 'price,desc' },
    { label: 'Tên A-Z', value: 'name,asc' },
  ];

  private productsService = inject(ProductsService);
  private cart = inject(CartService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  protected cdr = inject(ChangeDetectorRef);

  constructor() {
    this.filterForm = this.fb.group({
      search: [''],
      categoryId: [undefined],
      brandId: [undefined],
      minPrice: [undefined],
      maxPrice: [undefined],
      sort: ['id,desc'],
    });
  }

  ngOnInit(): void {
    this.productsService.getCategories().subscribe({
      next: (c) => {
        this.categories = c;
        this.cdr.markForCheck();
      }
    });
    this.productsService.getBrands().subscribe({
      next: (b) => {
        this.brands = b;
        this.cdr.markForCheck();
      }
    });
    this.loadProducts();
    
    // Subscribe to form changes for auto-filter
    this.filterForm.valueChanges.subscribe(() => {
      this.onFilterChange();
    });
  }

  loadProducts(page: number = 0): void {
    this.loading = true;
    this.cdr.markForCheck();
    this.currentPage = page;
    const filter: ProductFilter = {
      ...this.filterForm.value,
      page: page,
      size: 12,
    };
    this.productsService.getProducts(filter).subscribe({
      next: (data) => {
        this.products = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  onFilterChange(): void {
    this.loadProducts(0);
  }

  goToPage(page: number): void {
    this.loadProducts(page);
  }

  addToCart(product: ProductListItem): void {
    this.cart.addItem(product.id, 1);
    this.toast.success(`Đã thêm "${product.name}" vào giỏ hàng!`);
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('vi-VN').format(price) + ' ₫';
  }

  get pages(): number[] {
    const total = this.products?.totalPages ?? 0;
    return Array.from({ length: total }, (_, i) => i);
  }
}

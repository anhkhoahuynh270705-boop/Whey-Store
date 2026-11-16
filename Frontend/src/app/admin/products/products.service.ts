import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Brand,
  Category,
  PageResponse,
  Product,
  ProductFilter,
  ProductListItem,
} from '../../core/models/product.model';

@Injectable({ providedIn: 'root' })
export class AdminProductsService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  getProducts(filter: ProductFilter): Observable<PageResponse<ProductListItem>> {
    let params = new HttpParams();
    if (filter.search) params = params.set('search', filter.search);
    if (filter.categoryId) params = params.set('categoryId', filter.categoryId);
    if (filter.brandId) params = params.set('brandId', filter.brandId);
    if (filter.flavor) params = params.set('flavor', filter.flavor);
    if (filter.minPrice) params = params.set('minPrice', filter.minPrice);
    if (filter.maxPrice) params = params.set('maxPrice', filter.maxPrice);
    if (filter.page != null) params = params.set('page', filter.page);
    if (filter.size) params = params.set('size', filter.size);
    if (filter.sort) params = params.set('sort', filter.sort);
    if ((filter as any).includeInactive) params = params.set('includeInactive', true);
    return this.http.get<PageResponse<ProductListItem>>(`${this.api}/products`, { params });
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.api}/products/${id}`);
  }

  createProduct(body: Partial<Product>): Observable<Product> {
    return this.http.post<Product>(`${this.api}/products`, body);
  }

  updateProduct(id: number, body: Partial<Product>): Observable<Product> {
    return this.http.put<Product>(`${this.api}/products/${id}`, body);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/products/${id}`);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.api}/categories`);
  }

  createCategory(body: Partial<Category>): Observable<Category> {
    return this.http.post<Category>(`${this.api}/categories`, body);
  }

  updateCategory(id: number, body: Partial<Category>): Observable<Category> {
    return this.http.put<Category>(`${this.api}/categories/${id}`, body);
  }

  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/categories/${id}`);
  }

  getBrands(): Observable<Brand[]> {
    return this.http.get<Brand[]>(`${this.api}/brands`);
  }

  createBrand(body: Partial<Brand>): Observable<Brand> {
    return this.http.post<Brand>(`${this.api}/brands`, body);
  }

  updateBrand(id: number, body: Partial<Brand>): Observable<Brand> {
    return this.http.put<Brand>(`${this.api}/brands/${id}`, body);
  }

  deleteBrand(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/brands/${id}`);
  }
}

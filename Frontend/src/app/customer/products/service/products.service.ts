import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  Brand,
  Category,
  PageResponse,
  Product,
  ProductFilter,
  ProductListItem,
} from '../model/product.model';

@Injectable({ providedIn: 'root' })
export class ProductsService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  getProducts(filter: ProductFilter = {}): Observable<PageResponse<ProductListItem>> {
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
    return this.http.get<PageResponse<ProductListItem>>(`${this.api}/products`, { params });
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.api}/products/${id}`);
  }

  getProductBySlug(slug: string): Observable<Product> {
    return this.http.get<Product>(`${this.api}/products/slug/${slug}`);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.api}/categories`);
  }

  getBrands(): Observable<Brand[]> {
    return this.http.get<Brand[]>(`${this.api}/brands`);
  }
}

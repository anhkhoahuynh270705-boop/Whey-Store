import { Injectable } from '@angular/core';
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
} from '../models/product.model';
import { CartDto, CartItemRequest } from '../models/cart.model';
import { CheckoutRequest, CheckoutResponse, Order } from '../models/order.model';
import { InventoryItem } from '../models/inventory.model';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // PRODUCT 
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

  getProductBySlug(slug: string): Observable<Product> {
    return this.http.get<Product>(`${this.api}/products/slug/${slug}`);
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

  //  CART 
  private cartHeaders(guestId?: string): Record<string, string> {
    return guestId ? { 'X-Cart-Id': guestId } : {};
  }

  getCart(guestId?: string): Observable<CartDto> {
    return this.http.get<CartDto>(`${this.api}/cart`, { headers: this.cartHeaders(guestId) });
  }

  addToCart(item: CartItemRequest, guestId?: string): Observable<CartDto> {
    return this.http.post<CartDto>(`${this.api}/cart/items`, item,
      { headers: this.cartHeaders(guestId) });
  }

  updateCartItem(productId: number, quantity: number, guestId?: string): Observable<CartDto> {
    return this.http.put<CartDto>(`${this.api}/cart/items/${productId}`, null, {
      headers: this.cartHeaders(guestId),
      params: { quantity },
    });
  }

  removeFromCart(productId: number, guestId?: string): Observable<CartDto> {
    return this.http.delete<CartDto>(`${this.api}/cart/items/${productId}`,
      { headers: this.cartHeaders(guestId) });
  }

  clearCart(guestId?: string): Observable<CartDto> {
    return this.http.delete<CartDto>(`${this.api}/cart`,
      { headers: this.cartHeaders(guestId) });
  }

  mergeCart(guestId: string): Observable<CartDto> {
    return this.http.post<CartDto>(`${this.api}/cart/merge`, null, {
      headers: { 'X-Cart-Id': guestId },
    });
  }

  //  ORDER 
  checkout(request: CheckoutRequest): Observable<CheckoutResponse> {
    return this.http.post<CheckoutResponse>(`${this.api}/orders/checkout`, request);
  }

  getOrders(page = 0, size = 10): Observable<PageResponse<Order>> {
    return this.http.get<PageResponse<Order>>(`${this.api}/orders`, { params: { page, size } });
  }

  getAllOrders(page = 0, size = 10): Observable<PageResponse<Order>> {
    return this.http.get<PageResponse<Order>>(`${this.api}/orders`, {
      params: { page, size, all: true },
    });
  }

  getOrder(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.api}/orders/${id}`);
  }

  cancelOrder(id: number): Observable<Order> {
    return this.http.post<Order>(`${this.api}/orders/${id}/cancel`, null);
  }

  //  INVENTORY 
  getInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.api}/inventory`);
  }

  addStock(productId: number, delta: number): Observable<InventoryItem> {
    return this.http.put<InventoryItem>(`${this.api}/inventory/${productId}/add`, null, {
      params: { delta },
    });
  }

  //  REPORTS 
  downloadInvoice(orderId: number): Observable<Blob> {
    return this.http.get(`${this.api}/reports/invoice/${orderId}`, { responseType: 'blob' });
  }

  downloadRevenue(from: string, to: string, format = 'pdf'): Observable<Blob> {
    return this.http.get(`${this.api}/reports/revenue`, {
      responseType: 'blob',
      params: { from, to, format },
    });
  }

  downloadTopProducts(from: string, to: string, limit = 10, format = 'pdf'): Observable<Blob> {
    return this.http.get(`${this.api}/reports/top-products`, {
      responseType: 'blob',
      params: { from, to, limit, format },
    });
  }

  downloadInventoryReport(format = 'pdf'): Observable<Blob> {
    return this.http.get(`${this.api}/reports/inventory`, {
      responseType: 'blob',
      params: { format },
    });
  }
}

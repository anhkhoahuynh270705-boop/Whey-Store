import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { CartDto, CartItemRequest } from '../model/cart.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly GUEST_KEY = 'gymstore_cart_id';
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  private cartSubject = new BehaviorSubject<CartDto | null>(null);
  cart$ = this.cartSubject.asObservable();

  private cartHeaders(guestId?: string): HttpHeaders {
    return guestId ? new HttpHeaders({ 'X-Cart-Id': guestId }) : new HttpHeaders();
  }

  get guestId(): string | undefined {
    return localStorage.getItem(this.GUEST_KEY) ?? undefined;
  }

  set guestId(id: string) {
    localStorage.setItem(this.GUEST_KEY, id);
  }

  loadCart(): void {
    this.http
      .get<CartDto>(`${this.api}/cart`, {
        headers: this.cartHeaders(this.guestId),
        observe: 'response',
      })
      .subscribe({
        next: (res) => {
          const headerId = res.headers.get('X-Cart-Id');
          if (headerId) {
            this.guestId = headerId;
          }
          this.cartSubject.next(res.body);
        },
        error: () => this.cartSubject.next(null),
      });
  }

  addItem(productId: number, quantity = 1): void {
    const item: CartItemRequest = { productId, quantity };
    this.http
      .post<CartDto>(`${this.api}/cart/items`, item, {
        headers: this.cartHeaders(this.guestId),
        observe: 'response',
      })
      .subscribe({
        next: (res) => {
          const headerId = res.headers.get('X-Cart-Id');
          if (headerId) {
            this.guestId = headerId;
          }
          this.cartSubject.next(res.body);
        },
      });
  }

  updateItem(productId: number, quantity: number): void {
    const params = new HttpParams().set('quantity', quantity);
    this.http
      .put<CartDto>(`${this.api}/cart/items/${productId}`, null, {
        headers: this.cartHeaders(this.guestId),
        params,
      })
      .subscribe({
        next: (cart) => this.cartSubject.next(cart),
      });
  }

  removeItem(productId: number): void {
    this.http
      .delete<CartDto>(`${this.api}/cart/items/${productId}`, {
        headers: this.cartHeaders(this.guestId),
      })
      .subscribe({
        next: (cart) => this.cartSubject.next(cart),
      });
  }

  clearCart(): void {
    this.http
      .delete<CartDto>(`${this.api}/cart`, {
        headers: this.cartHeaders(this.guestId),
      })
      .subscribe({
        next: () => {
          this.cartSubject.next(null);
          localStorage.removeItem(this.GUEST_KEY);
        },
        error: () => {
          this.cartSubject.next(null);
          localStorage.removeItem(this.GUEST_KEY);
        },
      });
  }

  mergeOnLogin(): void {
    const guestId = this.guestId;
    if (!guestId) {
      this.loadCart();
      return;
    }
    this.http
      .post<CartDto>(`${this.api}/cart/merge`, null, {
        headers: new HttpHeaders({ 'X-Cart-Id': guestId }),
      })
      .subscribe({
        next: (cart) => {
          this.cartSubject.next(cart);
          localStorage.removeItem(this.GUEST_KEY);
        },
        error: () => {
          localStorage.removeItem(this.GUEST_KEY);
          this.loadCart();
        },
      });
  }

  get totalQuantity(): number {
    return this.cartSubject.value?.totalQuantity ?? 0;
  }
}

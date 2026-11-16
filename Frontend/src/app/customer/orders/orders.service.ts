import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order, PageResponse } from '../../core/models/order.model';

@Injectable({ providedIn: 'root' })
export class OrdersService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  getOrders(page = 0, size = 10): Observable<PageResponse<Order>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Order>>(`${this.api}/orders`, { params });
  }

  getOrder(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.api}/orders/${id}`);
  }

  cancelOrder(id: number): Observable<Order> {
    return this.http.post<Order>(`${this.api}/orders/${id}/cancel`, null);
  }

  downloadInvoice(orderId: number): Observable<Blob> {
    return this.http.get(`${this.api}/reports/invoice/${orderId}`, { responseType: 'blob' });
  }
}

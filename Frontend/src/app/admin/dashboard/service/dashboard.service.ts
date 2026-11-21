import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Order } from '../../orders/model/order.model';
import { InventoryItem } from '../../inventory/model/inventory.model';
import { PageResponse, ProductListItem } from '../../products/model/product.model';

@Injectable({ providedIn: 'root' })
export class AdminDashboardService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  getRecentOrders(page = 0, size = 5): Observable<PageResponse<Order>> {
    const params = new HttpParams().set('page', page).set('size', size).set('all', true);
    return this.http.get<PageResponse<Order>>(`${this.api}/orders`, { params });
  }

  getProductCount(): Observable<PageResponse<ProductListItem>> {
    const params = new HttpParams().set('page', 0).set('size', 1);
    return this.http.get<PageResponse<ProductListItem>>(`${this.api}/products`, { params });
  }

  getInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.api}/inventory`);
  }
}

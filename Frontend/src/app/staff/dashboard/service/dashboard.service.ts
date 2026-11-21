import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Order } from '../../orders/model/order.model';
import { InventoryItem } from '../../inventory/model/inventory.model';
import { PageResponse } from '../../orders/model/order.model';

@Injectable({ providedIn: 'root' })
export class StaffDashboardService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  getAllOrders(page = 0, size = 50): Observable<PageResponse<Order>> {
    const params = new HttpParams().set('page', page).set('size', size).set('all', true);
    return this.http.get<PageResponse<Order>>(`${this.api}/orders`, { params });
  }

  getInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.api}/inventory`);
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { InventoryItem } from '../model/inventory.model';

@Injectable({ providedIn: 'root' })
export class AdminInventoryService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  getInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.api}/inventory`);
  }

  createItem(productId: number, sku: string, availableQty: number): Observable<InventoryItem> {
    return this.http.post<InventoryItem>(`${this.api}/inventory`, { productId, sku, availableQty });
  }

  addStock(productId: number, delta: number): Observable<InventoryItem> {
    return this.http.put<InventoryItem>(`${this.api}/inventory/${productId}/add`, null, {
      params: { delta },
    });
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { InventoryItem } from '../model/inventory.model';


@Injectable({ providedIn: 'root' })
export class StaffInventoryService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  getInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.api}/inventory`);
  }
}

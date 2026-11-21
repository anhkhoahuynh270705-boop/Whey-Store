import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponse } from '../model/customer.model';
import { environment } from '../../../../environments/environment';
import { Order } from '../model/customer.model';

@Injectable({ providedIn: 'root' })
export class StaffCustomersService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  getAllOrders(page = 0, size = 100): Observable<PageResponse<Order>> {
    const params = new HttpParams().set('page', page).set('size', size).set('all', true);
    return this.http.get<PageResponse<Order>>(`${this.api}/orders`, { params });
  }
}

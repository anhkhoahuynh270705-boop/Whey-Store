import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AdminReportsService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

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

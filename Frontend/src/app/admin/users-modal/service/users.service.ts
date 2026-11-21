import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { UserAccount } from '../model/users.model';

@Injectable({ providedIn: 'root' })
export class AdminUsersService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  getUsers(): Observable<UserAccount[]> {
    return this.http.get<UserAccount[]>(`${this.api}/admin/users`);
  }

  toggleStatus(id: string): Observable<UserAccount> {
    return this.http.patch<UserAccount>(`${this.api}/admin/users/${id}/toggle-status`, {});
  }

  createUser(payload: { username: string; email: string; fullName: string; role: 'ADMIN' | 'STAFF' | 'CUSTOMER'; password?: string }): Observable<UserAccount> {
    return this.http.post<UserAccount>(`${this.api}/admin/users`, payload);
  }
}

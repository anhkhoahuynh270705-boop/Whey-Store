import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface UserAccount {
  id: string;
  username: string;
  email: string;
  fullName: string;
  role: 'ADMIN' | 'STAFF' | 'CUSTOMER';
  status: 'ACTIVE' | 'DISABLED';
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class AdminUsersService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  private mockUsers: UserAccount[] = [
    {
      id: 'usr-001',
      username: 'admin',
      email: 'admin@gymstore.vn',
      fullName: 'Quản trị viên Hệ thống',
      role: 'ADMIN',
      status: 'ACTIVE',
      createdAt: '2026-11-14 10:00',
    },
    {
      id: 'usr-002',
      username: 'staff',
      email: 'staff@gymstore.vn',
      fullName: 'Nhân viên Quản lý Kho',
      role: 'STAFF',
      status: 'ACTIVE',
      createdAt: '2026-11-14 10:30',
    },
    {
      id: 'usr-003',
      username: 'customer',
      email: 'customer@gymstore.vn',
      fullName: 'Nguyễn Văn A',
      role: 'CUSTOMER',
      status: 'ACTIVE',
      createdAt: '2026-11-15 08:15',
    },
  ];

  getUsers(): Observable<UserAccount[]> {
    return of([...this.mockUsers]);
  }

  toggleStatus(id: string): Observable<UserAccount> {
    const user = this.mockUsers.find((u) => u.id === id);
    if (user) {
      user.status = user.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE';
      return of({ ...user });
    }
    throw new Error('Không tìm thấy tài khoản');
  }

  createUser(payload: { username: string; email: string; fullName: string; role: 'ADMIN' | 'STAFF' | 'CUSTOMER' }): Observable<UserAccount> {
    const newUser: UserAccount = {
      id: `usr-${String(this.mockUsers.length + 1).padStart(3, '0')}`,
      username: payload.username,
      email: payload.email,
      fullName: payload.fullName,
      role: payload.role,
      status: 'ACTIVE',
      createdAt: new Date().toISOString().replace('T', ' ').substring(0, 16),
    };
    this.mockUsers.unshift(newUser);
    return of(newUser);
  }
}

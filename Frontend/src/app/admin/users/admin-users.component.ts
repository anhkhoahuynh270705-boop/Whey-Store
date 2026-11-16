import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminUsersService, UserAccount } from './users.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users.component.html',
  styleUrl: './admin-users.component.scss',
})
export class AdminUsersComponent implements OnInit {
  protected cdr = inject(ChangeDetectorRef);
  private usersService = inject(AdminUsersService);
  private toast = inject(ToastService);

  users: UserAccount[] = [];
  search = '';
  roleFilter = '';
  showCreateModal = false;

  newUsername = '';
  newFullName = '';
  newEmail = '';
  newRole: 'ADMIN' | 'STAFF' | 'CUSTOMER' = 'STAFF';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.usersService.getUsers().subscribe((u) => {
      this.users = u;
      this.cdr.markForCheck();
    });
  }

  get filteredUsers(): UserAccount[] {
    return this.users.filter((u) => {
      const matchSearch =
        !this.search ||
        u.username.toLowerCase().includes(this.search.toLowerCase()) ||
        u.fullName.toLowerCase().includes(this.search.toLowerCase()) ||
        u.email.toLowerCase().includes(this.search.toLowerCase());

      const matchRole = !this.roleFilter || u.role === this.roleFilter;
      return matchSearch && matchRole;
    });
  }

  toggleStatus(u: UserAccount): void {
    this.usersService.toggleStatus(u.id).subscribe({
      next: (updated) => {
        const action = updated.status === 'ACTIVE' ? 'Kích hoạt' : 'Khóa';
        this.toast.success(`Đã ${action} tài khoản "${updated.username}" thành công!`);
        this.load();
      },
      error: () => {
        this.toast.error('Lỗi khi cập nhật trạng thái tài khoản!');
      },
    });
  }

  openCreate(): void {
    this.newUsername = '';
    this.newFullName = '';
    this.newEmail = '';
    this.newRole = 'STAFF';
    this.showCreateModal = true;
  }

  closeCreate(): void {
    this.showCreateModal = false;
  }

  submitCreate(): void {
    if (!this.newUsername.trim() || !this.newFullName.trim() || !this.newEmail.trim()) {
      this.toast.warning('Vui lòng điền đầy đủ các thông tin bắt buộc');
      return;
    }

    this.usersService
      .createUser({
        username: this.newUsername.trim(),
        fullName: this.newFullName.trim(),
        email: this.newEmail.trim(),
        role: this.newRole,
      })
      .subscribe({
        next: (created) => {
          this.toast.success(`Đã tạo tài khoản "${created.username}" thành công!`);
          this.showCreateModal = false;
          this.load();
        },
        error: () => {
          this.toast.error('Không thể tạo tài khoản!');
        },
      });
  }

  getRoleBadge(role: string): string {
    switch (role) {
      case 'ADMIN':
        return 'badge badge-danger';
      case 'STAFF':
        return 'badge badge-primary';
      case 'CUSTOMER':
        return 'badge badge-success';
      default:
        return 'badge badge-muted';
    }
  }

  getRoleLabel(role: string): string {
    switch (role) {
      case 'ADMIN':
        return 'Quản trị viên';
      case 'STAFF':
        return 'Nhân viên';
      case 'CUSTOMER':
        return 'Khách hàng';
      default:
        return role;
    }
  }
}

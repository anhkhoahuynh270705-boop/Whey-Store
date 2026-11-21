import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminUsersService } from '../service/users.service';
import { ToastService } from '../../../shared/toast/service/toast.service';
import { UserAccount } from '../model/users.model';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-users.component.html',
  styleUrl: './admin-users.component.scss',
})
export class AdminUsersComponent implements OnInit {
  protected cdr = inject(ChangeDetectorRef);
  private usersService = inject(AdminUsersService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);

  users: UserAccount[] = [];
  showCreateModal = false;

  filterForm = this.fb.nonNullable.group({
    search: [''],
    roleFilter: [''],
  });

  createForm = this.fb.nonNullable.group({
    username: ['', [Validators.required, this.noBlank]],
    fullName: ['', [Validators.required, this.noBlank]],
    email: ['', [Validators.required, Validators.email]],
    role: ['STAFF' as 'ADMIN' | 'STAFF' | 'CUSTOMER'],
  });

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
    const { search, roleFilter } = this.filterForm.getRawValue();
    const keyword = search.trim().toLowerCase();
    return this.users.filter((u) => {
      const matchSearch =
        !keyword ||
        u.username.toLowerCase().includes(keyword) ||
        u.fullName.toLowerCase().includes(keyword) ||
        u.email.toLowerCase().includes(keyword);

      const matchRole = !roleFilter || u.role === roleFilter;
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
      error: () => this.toast.error('Lỗi khi cập nhật trạng thái tài khoản!'),
    });
  }

  openCreate(): void {
    this.createForm.reset({ role: 'STAFF' as 'ADMIN' | 'STAFF' | 'CUSTOMER' });
    this.showCreateModal = true;
  }

  closeCreate(): void {
    this.showCreateModal = false;
  }

  submitCreate(): void {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      this.toast.warning('Vui lòng điền đầy đủ các thông tin bắt buộc');
      return;
    }

    const v = this.createForm.getRawValue();
    this.usersService
      .createUser({
        username: v.username.trim(),
        fullName: v.fullName.trim(),
        email: v.email.trim(),
        role: v.role,
      })
      .subscribe({
        next: (created) => {
          this.toast.success(`Đã tạo tài khoản "${created.username}" thành công!`);
          this.showCreateModal = false;
          this.load();
        },
        error: () => this.toast.error('Không thể tạo tài khoản!'),
      });
  }
  private noBlank(control: { value: string }) {
    return control.value && !control.value.trim() ? { blank: true } : null;
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

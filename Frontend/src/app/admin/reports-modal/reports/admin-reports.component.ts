import { Component, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { AdminReportsService } from '../service/reports.service';
import { ToastService } from '../../../shared/toast/service/toast.service';

@Component({
  selector: 'app-admin-reports',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-reports.component.html',
  styleUrl: './admin-reports.component.scss',
})
export class AdminReportsComponent {
  private reportsService = inject(AdminReportsService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  protected cdr = inject(ChangeDetectorRef);
  loading = false;

  form = this.fb.group({
    from: [new Date().toISOString().slice(0, 8) + '01', Validators.required],
    to: [new Date().toISOString().slice(0, 10), Validators.required],
    format: ['pdf', Validators.required],
    limit: [10, [Validators.required, Validators.min(1)]],
  });

  private download(obs: any, name: string): void {
    this.loading = true;
    this.cdr.markForCheck();
    obs.subscribe({
      next: (blob: Blob) => {
        const url = URL.createObjectURL(blob);
        Object.assign(document.createElement('a'), { href: url, download: name }).click();
        URL.revokeObjectURL(url);
        this.toast.success(`Đã tải báo cáo ${name} thành công!`);
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  revenue(): void {
    const v = this.form.value;
    this.download(
      this.reportsService.downloadRevenue(v.from!, v.to!, v.format!),
      `revenue.${v.format}`,
    );
  }
  topProducts(): void {
    const v = this.form.value;
    this.download(
      this.reportsService.downloadTopProducts(v.from!, v.to!, v.limit!, v.format!),
      `top-products.${v.format}`,
    );
  }
  inventory(): void {
    const v = this.form.value;
    this.download(this.reportsService.downloadInventoryReport(v.format!), `inventory.${v.format}`);
  }
}

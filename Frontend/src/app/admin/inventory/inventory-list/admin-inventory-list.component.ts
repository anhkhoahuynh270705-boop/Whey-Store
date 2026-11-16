import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminInventoryService } from '../inventory.service';
import { ToastService } from '../../../core/services/toast.service';
import { InventoryItem } from '../../../core/models/inventory.model';

@Component({
  selector: 'app-admin-inventory-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-inventory-list.component.html',
  styleUrl: './admin-inventory-list.component.scss',
})
export class AdminInventoryListComponent implements OnInit {
  items: InventoryItem[] = [];
  loading = true;
  deltaMap: Record<number, number> = {};
  private inventoryService = inject(AdminInventoryService);
  private toast = inject(ToastService);
  protected cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.inventoryService.getInventory().subscribe({
      next: (d) => {
        this.items = d;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  addStock(productId: number): void {
    const delta = this.deltaMap[productId] ?? 0;
    if (delta <= 0) return;
    this.inventoryService.addStock(productId, delta).subscribe({
      next: () => {
        this.deltaMap[productId] = 0;
        this.toast.success(`Đã nhập thêm +${delta} vào kho thành công!`);
        this.load();
        this.cdr.markForCheck();
      },
      error: () => {
        this.cdr.markForCheck();
      },
    });
  }
}

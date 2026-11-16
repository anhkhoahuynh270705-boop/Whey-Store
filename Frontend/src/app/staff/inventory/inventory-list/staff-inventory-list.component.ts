import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StaffInventoryService } from '../inventory.service';
import { InventoryItem } from '../../../core/models/inventory.model';

@Component({
  selector: 'app-staff-inventory-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './staff-inventory-list.component.html',
  styleUrl: './staff-inventory-list.component.scss',
})
export class StaffInventoryListComponent implements OnInit {
  items: InventoryItem[] = [];
  loading = true;
  search = '';
  private inventoryService = inject(StaffInventoryService);
  protected cdr = inject(ChangeDetectorRef);

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.inventoryService.getInventory().subscribe({
      next: d => {
        this.items = d;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  get filtered(): InventoryItem[] {
    const q = this.search.toLowerCase();
    return !q ? this.items : this.items.filter(i => i.sku.toLowerCase().includes(q));
  }

  get lowStock(): number { return this.items.filter(i => i.availableQty < 10).length; }
}

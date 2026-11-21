import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormArray,
  FormBuilder,
  FormControl,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { InventoryItem } from '../model/inventory.model';
import { AdminInventoryService } from '../service/inventory.service';
import { ToastService } from '../../../shared/toast/service/toast.service';

@Component({
  selector: 'app-admin-inventory-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
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
  private fb = inject(FormBuilder);

  deltas = new FormArray<FormControl<number>>([]);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.inventoryService.getInventory().subscribe({
      next: (d) => {
        this.items = d;
        this.deltas.clear();
        d.forEach(() =>
          this.deltas.push(
            this.fb.nonNullable.control(1, [
              Validators.required,
              Validators.min(1),
              Validators.pattern(/^\d+$/),
            ]),
          ),
        );
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

   addStock(index: number): void {
    const control = this.deltas.at(index);
    if (control.invalid) {
      control.markAsTouched();
      return;
    }

    const item = this.items[index];
    const delta = control.value;

    this.inventoryService.addStock(item.productId, delta).subscribe({
      next: () => {
        this.toast.success(`Đã nhập thêm +${delta} vào kho thành công!`);
        this.load(); // tải lại, đồng thời đặt lại ô nhập về 1
      },
    });
  }
}

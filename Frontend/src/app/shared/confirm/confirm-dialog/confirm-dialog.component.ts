import { Component, inject, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfirmDialogService, ConfirmVariant } from '../service/confirm-dialog.service';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirm-dialog.component.html',
  styleUrl: './confirm-dialog.component.scss',
})
export class ConfirmDialogComponent {
  readonly dialog = inject(ConfirmDialogService);

  /** Map variant → icon class */
  iconFor(variant: ConfirmVariant = 'danger'): string {
    const map: Record<ConfirmVariant, string> = {
      danger: 'fas fa-trash-alt',
      warning: 'fas fa-triangle-exclamation',
      info: 'fas fa-circle-info',
      success: 'fas fa-circle-check',
    };
    return map[variant];
  }

  /** Đóng khi bấm Escape */
  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.dialog.state()) this.dialog.cancel();
  }
}

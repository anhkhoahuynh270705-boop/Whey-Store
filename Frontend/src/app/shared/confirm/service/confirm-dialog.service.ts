import { Injectable, signal } from '@angular/core';
import { Subject } from 'rxjs';
import { ConfirmOptions } from '../model/confirm-dialog.model';

export type ConfirmVariant = 'danger' | 'warning' | 'info' | 'success';


interface DialogState extends ConfirmOptions {
  resolve: (result: boolean) => void;
}

@Injectable({ providedIn: 'root' })
export class ConfirmDialogService {
  readonly state = signal<DialogState | null>(null);

  open(options: ConfirmOptions): Promise<boolean> {
    return new Promise<boolean>((resolve) => {
      this.state.set({ ...options, resolve });
    });
  }

  confirm(): void {
    const s = this.state();
    if (s) {
      s.resolve(true);
      this.state.set(null);
    }
  }

  cancel(): void {
    const s = this.state();
    if (s) {
      s.resolve(false);
      this.state.set(null);
    }
  }
}

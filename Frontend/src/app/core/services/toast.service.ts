import { Injectable, signal } from '@angular/core';
import { Toast, ToastType } from '../models/toast.model';

@Injectable({ providedIn: 'root' })
export class ToastService {
  private toastsSignal = signal<Toast[]>([]);
  readonly toasts = this.toastsSignal.asReadonly();

  private defaultIcons: Record<ToastType, string> = {
    success: 'fas fa-check-circle',
    error: 'fas fa-exclamation-circle',
    warning: 'fas fa-exclamation-triangle',
    info: 'fas fa-info-circle',
  };

  show(
    type: ToastType,
    message: string,
    title?: string,
    duration = 4000
  ): string {
    const id = 'toast_' + Date.now() + '_' + Math.random().toString(36).substring(2, 7);
    const defaultTitle =
      type === 'success'
        ? 'Thành công'
        : type === 'error'
        ? 'Lỗi'
        : type === 'warning'
        ? 'Cảnh báo'
        : 'Thông báo';

    const toast: Toast = {
      id,
      type,
      title: title || defaultTitle,
      message,
      duration,
      icon: this.defaultIcons[type],
    };

    this.toastsSignal.update((current) => [...current, toast]);

    if (duration > 0) {
      setTimeout(() => {
        this.remove(id);
      }, duration);
    }

    return id;
  }

  success(message: string, title?: string, duration = 3500): string {
    return this.show('success', message, title, duration);
  }

  error(message: string, title?: string, duration = 4500): string {
    return this.show('error', message, title, duration);
  }

  warning(message: string, title?: string, duration = 4000): string {
    return this.show('warning', message, title, duration);
  }

  info(message: string, title?: string, duration = 3500): string {
    return this.show('info', message, title, duration);
  }

  remove(id: string): void {
    this.toastsSignal.update((current) => current.filter((t) => t.id !== id));
  }

  clear(): void {
    this.toastsSignal.set([]);
  }
}

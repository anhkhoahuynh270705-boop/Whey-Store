import { HttpErrorResponse, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';

export const errorInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
) => {
  const toast = inject(ToastService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      console.group(`[HTTP ERROR] ${req.method} ${req.url}`);
      console.error(`Status: ${error.status} ${error.statusText || ''}`);
      if (error.error) {
        console.error('Response Body:', error.error);
      }
      console.error('Full Error Object:', error);
      console.groupEnd();

      // Skip silent 404s for guest cart checking
      const isSilentCart404 = req.url.includes('/cart') && error.status === 404;

      if (!isSilentCart404) {
        let msg = error.error?.message;
        if (!msg) {
          if (error.status === 0) {
            msg = 'Không thể kết nối đến máy chủ. Vui lòng kiểm tra backend hoặc kết nối mạng.';
          } else if (error.status === 401) {
            msg = 'Phiên đăng nhập đã hết hạn hoặc chưa xác thực.';
          } else if (error.status === 403) {
            msg = 'Bạn không có quyền thực hiện thao tác này.';
          } else if (error.status === 404) {
            msg = 'Không tìm thấy dữ liệu yêu cầu.';
          } else if (error.status >= 500) {
            msg = 'Máy chủ gặp sự cố nội bộ. Vui lòng thử lại sau.';
          } else {
            msg = error.statusText || 'Có lỗi xảy ra trong quá trình xử lý.';
          }
        }
        toast.error(msg, 'Lỗi yêu cầu');
      }

      return throwError(() => error);
    }),
  );
};

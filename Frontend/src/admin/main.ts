import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors, withFetch } from '@angular/common/http';
import { inject, provideAppInitializer } from '@angular/core';
import { adminRoutes } from '../app/admin/admin.routes';
import { authInterceptor } from '../app/core/interceptors/auth.interceptor';
import { errorInterceptor } from '../app/core/interceptors/error.interceptor';
import { AuthService } from '../app/core/services/auth.service';
import { AdminLayoutComponent } from '../app/admin/layout/admin-layout.component';

bootstrapApplication(AdminLayoutComponent, {
  providers: [
    provideRouter(adminRoutes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor]), withFetch()),
    provideAppInitializer(() => inject(AuthService).init()),
  ],
}).catch(err => console.error(err));

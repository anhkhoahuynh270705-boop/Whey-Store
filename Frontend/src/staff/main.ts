import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors, withFetch } from '@angular/common/http';
import { inject, provideAppInitializer } from '@angular/core';
import { staffRoutes } from '../app/staff/staff.routes';
import { authInterceptor } from '../app/core/interceptors/auth.interceptor';
import { errorInterceptor } from '../app/core/interceptors/error.interceptor';
import { AuthService } from '../app/core/services/auth.service';
import { StaffLayoutComponent } from '../app/staff/layout/staff-layout.component';

bootstrapApplication(StaffLayoutComponent, {
  providers: [
    provideRouter(staffRoutes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor]), withFetch()),
    provideAppInitializer(() => inject(AuthService).init('login-required')),
  ],
}).catch((err) => console.error(err));

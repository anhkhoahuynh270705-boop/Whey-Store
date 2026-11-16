import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export function roleGuard(...roles: string[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    if (!auth.isLoggedIn) {
      auth.login();
      return false;
    }
    const hasRole = roles.some((r) => auth.hasRole(r));
    if (!hasRole) {
      return router.createUrlTree(['/unauthorized']);
    }
    return true;
  };
}

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  if (!auth.isLoggedIn) {
    auth.login();
    return false;
  }
  return true;
};

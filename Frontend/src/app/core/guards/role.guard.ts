import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export function roleGuard(...roles: string[]): CanActivateFn {
  return async () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    await auth.waitForInit();

    if (!auth.isLoggedIn) {
      auth.login(window.location.href);
      return false;
    }

    const hasRole = roles.some((r) => auth.hasRole(r));
    if (!hasRole) {
      return router.createUrlTree(['/unauthorized']);
    }

    return true;
  };
}

export const authGuard: CanActivateFn = async () => {
  const auth = inject(AuthService);

  await auth.waitForInit();

  if (!auth.isLoggedIn) {
    auth.login(window.location.href);
    return false;
  }
  return true;
};

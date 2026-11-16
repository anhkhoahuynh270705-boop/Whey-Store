import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private keycloak: Keycloak;

  constructor() {
    this.keycloak = new Keycloak({
      url: environment.keycloakUrl,
      realm: environment.keycloakRealm,
      clientId: environment.keycloakClientId,
    });
  }

  /** Gọi 1 lần khi app khởi động. */
  init(): Promise<boolean> {
    return this.keycloak.init({
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: `${window.location.origin}/silent-check.html`,
      pkceMethod: 'S256',
    }).catch(err => {
      console.warn('Keycloak SSO init failed, proceeding as unauthenticated:', err);
      return false;
    });
  }

  login(): void {
    this.keycloak.login({ redirectUri: window.location.origin + '/home' });
  }

  logout(): void {
    this.keycloak.logout({ redirectUri: window.location.origin });
  }

  register(): void {
    this.keycloak.register();
  }

  get isLoggedIn(): boolean {
    return !!this.keycloak.authenticated;
  }

  get username(): string | undefined {
    return this.keycloak.tokenParsed?.['preferred_username'];
  }

  get fullName(): string | undefined {
    const p = this.keycloak.tokenParsed as any;
    return p ? `${p['given_name'] ?? ''} ${p['family_name'] ?? ''}`.trim() : undefined;
  }

  get roles(): string[] {
    return this.keycloak.tokenParsed?.['realm_access']?.['roles'] ?? [];
  }

  hasRole(role: string): boolean {
    return this.roles.includes(role);
  }

  /** Lấy token hiện tại (auto-refresh nếu sắp hết hạn). */
  async getToken(): Promise<string | undefined> {
    try {
      await this.keycloak.updateToken(30);
      return this.keycloak.token;
    } catch {
      return undefined;
    }
  }
}

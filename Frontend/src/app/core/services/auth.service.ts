import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private keycloak: Keycloak;
  private _initDone = false;
  private _initPromise: Promise<boolean> | null = null;

  constructor() {
    this.keycloak = new Keycloak({
      url: environment.keycloakUrl,
      realm: environment.keycloakRealm,
      clientId: environment.keycloakClientId,
    });
  }

  init(mode: 'check-sso' | 'login-required' = 'check-sso'): Promise<boolean> {
    this._initPromise = this.keycloak
      .init({
        onLoad: mode,
        silentCheckSsoRedirectUri: `${window.location.origin}/silent-check.html`,
        pkceMethod: 'S256',
      })
      .then((auth) => {
        this._initDone = true;
        return auth;
      })
      .catch((err) => {
        console.warn('Keycloak SSO init failed:', err);
        this._initDone = true;
        return false;
      });
    return this._initPromise;
  }

  waitForInit(): Promise<boolean> {
    if (this._initDone) return Promise.resolve(true);
    if (this._initPromise) return this._initPromise.then(() => true);
    return Promise.resolve(true);
  }

  login(redirectUri?: string, loginHint?: string): void {
    this.keycloak.login({
      redirectUri: redirectUri || window.location.origin + '/home',
      loginHint,
    });
  }

  logout(redirectUri?: string): void {
    const refreshToken = this.keycloak.refreshToken;
    const targetUri = redirectUri || window.location.origin;

    if (refreshToken) {
      fetch(`${environment.apiUrl}/auth/logout`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
        keepalive: true,
      }).catch(() => {});
    }

    this.keycloak.logout({ redirectUri: targetUri });
  }

  register(redirectUri?: string): void {
    this.keycloak.login({
      action: 'register',
      redirectUri: redirectUri || window.location.origin + '/home',
    });
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

  async getToken(): Promise<string | undefined> {
    try {
      await this.keycloak.updateToken(30);
      return this.keycloak.token;
    } catch {
      return undefined;
    }
  }
}

import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [],
  templateUrl: './unauthorized.component.html',
  styleUrl: './unauthorized.component.scss',
})
export class UnauthorizedComponent {
  private auth = inject(AuthService);

  goHome(): void {
    window.location.href = window.location.origin + '/home';
  }

  login(): void {
    this.auth.login(window.location.href);
  }
}

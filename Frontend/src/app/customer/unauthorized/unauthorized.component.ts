import { Component, ChangeDetectorRef, inject } from '@angular/core';
import { Router } from '@angular/router';
@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [],
  templateUrl: './unauthorized.component.html',
  styleUrl: './unauthorized.component.scss',
})
export class UnauthorizedComponent {
  private router = inject(Router);
  protected cdr = inject(ChangeDetectorRef);
  goHome(): void { this.router.navigate(['/']); }
  login(): void { this.router.navigate(['/auth/login']); }
}

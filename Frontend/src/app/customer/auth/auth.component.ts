import { Component, OnInit, inject } from '@angular/core';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.scss',
})
export class AuthComponent implements OnInit {
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  ngOnInit(): void {
    if (this.auth.isLoggedIn) {
      this.router.navigate(['/home']);
      return;
    }

    const path = this.route.snapshot.url[0]?.path;
    if (path === 'register') {
      this.auth.register(window.location.origin + '/home');
    } else {
      this.auth.login(window.location.origin + '/home');
    }
  }

  goLogin(): void {
    this.auth.login(window.location.origin + '/home');
  }

  goRegister(): void {
    this.auth.register(window.location.origin + '/home');
  }
}

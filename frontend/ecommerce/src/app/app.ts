import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterOutlet, NavigationEnd } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {

  private cookieService = inject(CookieService);
  private router = inject(Router);

  isAuthenticated = signal(this.cookieService.check("Authorization"));

  constructor() {
    // 🔥 Re-check auth on every route change
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.isAuthenticated.set(
          this.cookieService.check("Authorization")
        );
      });
  }

  logout() {
    this.cookieService.delete("Authorization");
    this.isAuthenticated.set(false);
    this.router.navigate(["/login"]);
  }
}

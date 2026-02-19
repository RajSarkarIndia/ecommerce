import {Component, inject, signal} from '@angular/core';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {CookieService} from 'ngx-cookie-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('ecommerce');
  cookieService:CookieService = inject(CookieService);
  router:Router=inject(Router);

  logout(){

this.cookieService.delete("Authorization");
this.router.navigate(["/login"]);


  }



}

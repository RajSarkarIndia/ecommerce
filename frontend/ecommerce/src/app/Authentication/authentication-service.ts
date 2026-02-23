import {inject, Injectable} from '@angular/core';
import {CookieService} from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  cookie:CookieService=inject(CookieService);




   isAuthenticated(): boolean {
 return this.cookie.check('Authorization');

   }
}

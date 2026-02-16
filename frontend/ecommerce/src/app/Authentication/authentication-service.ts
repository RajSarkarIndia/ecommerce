import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private authenticated: boolean = false;


   setAuthenticated(authenticated: boolean): void {
    this.authenticated = authenticated;

  }

   isAuthenticated(): boolean {

    return this.authenticated;
  }

}

import {ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot} from '@angular/router';

import {inject} from '@angular/core';
import {AuthenticationService} from '../Authentication/authentication-service';
import { Router } from '@angular/router';

export const authenticationGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {

  const authenticationService = inject(AuthenticationService);
  const router = inject(Router);

  if (authenticationService.isAuthenticated()) {
    return true;
  } else {
    router.navigate(['/login']);
    return false;
  }


};

import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {TokenService} from '../../services/token/token-service';

export const recruiterGuard: CanActivateFn = (route, state) => {
  const router=inject(Router);
  const tokenService=inject(TokenService);
  const token=tokenService.token;

  if(!token){
    router.navigate(['/login']);
    return false;
  }

  const payload=JSON.parse(atob(token.split('.')[1]));
  const role=payload.role;

  if(role ==='RECRUITER'){
    return true
  }
  router.navigate(['/home'])
  return false
};

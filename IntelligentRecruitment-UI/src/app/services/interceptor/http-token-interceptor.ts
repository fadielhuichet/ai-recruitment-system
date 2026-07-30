import {HttpHeaders, HttpInterceptorFn} from '@angular/common/http';
import {inject, Inject} from '@angular/core';
import {Router} from '@angular/router';
import {catchError, throwError} from 'rxjs';

export const httpTokenInterceptor: HttpInterceptorFn = (req, next) => {

  const router=inject(Router)
  const token=localStorage.getItem('token');


  if(token){
    const payload = JSON.parse(atob(token.split('.')[1]));
    const clonedReq= req.clone({
      setHeaders:{
        Authorization: `Bearer ${token}`,
        'X-User-Email': payload.sub,
      }
    });
    return next(clonedReq).pipe(
      catchError((error)=>{
        if(error.status === 401){
          localStorage.removeItem('token');
          router.navigate(['/login'])
        }else if (error.status === 403){
          router.navigate(['/login'])

        }
        return throwError(()=>error);
      })
    )
  }


  return next(req);
};

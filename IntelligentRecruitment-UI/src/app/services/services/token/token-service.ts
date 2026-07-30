import { Injectable } from '@angular/core';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class TokenService {


  constructor(private router:Router) {
  }

  set token(token:string){
    localStorage.setItem('token',token);
  }

  get token(){
    return localStorage.getItem('token')as string;
  }

  logout(){
    localStorage.removeItem('token');
    this.router.navigate(['/login']);

  }

  hasToken():Boolean{
    return !!localStorage.getItem('token');
  }

}

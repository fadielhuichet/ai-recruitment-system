import { Component } from '@angular/core';
import { registerRequest } from '../../models/Dto/Auth/registerRequest';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from "@angular/router";
import { Login } from '../login/login';
import { AuthenticationService } from '../../services/services/Authentication-service/authentication-service';
import Swal from 'sweetalert2';
import {Header} from '../../Shared/header/header';
import {email} from '@angular/forms/signals';
@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, Header],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  registerRequest: registerRequest={email:'',password:'',firstName:'',lastName:'',companyName:'',phone:''}
  errorMsg:Array<String>=[];
  confirmPassword:String='';
  constructor(private router:Router,private authService:AuthenticationService){}

  onSubmit(form:any){
    if(form.valid && this.registerRequest.password === this.confirmPassword){
      this.authService.register(this.registerRequest).subscribe({
        next:()=>{
          Swal.fire({
            icon: 'success',
            title: 'Register Successful',
            text: 'Redirecting to the Home page',
            timer: 2000,
            showConfirmButton: false,
          });
        },
        error:(err)=>{
          this.errorMsg=err.error.message;

        }
      })

    }

  }

  signIn(){
    this.router.navigate(['/login'])
  }

}

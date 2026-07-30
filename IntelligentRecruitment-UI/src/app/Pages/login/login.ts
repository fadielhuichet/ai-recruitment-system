import {ChangeDetectorRef, Component, NgModule, NgZone} from '@angular/core';
import { LoginRequest } from '../../models/Dto/Auth/loginRequest';
import { CommonModule } from '@angular/common';
import {FormsModule}  from '@angular/forms'
import {Router, RouterLink} from '@angular/router';
import { AuthenticationService } from '../../services/services/Authentication-service/authentication-service';
import Swal from 'sweetalert2';
import {Header} from '../../Shared/header/header';
import {TokenService} from '../../services/services/token/token-service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, Header, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  loginRequest: LoginRequest={email:'', password:''};
  errorMsg: String|null=null;
  isRecruiters=false;
   showRoleModal=false;


  constructor(
    private authApi: AuthenticationService ,
    private router:Router ,
    private cdr:ChangeDetectorRef,
    private tokenService:TokenService){}

  login(form:any){
    if(form.valid){
    this.errorMsg=null;
    this.authApi.login(this.loginRequest).subscribe({
      next:(res) =>{
        this.tokenService.token=res.token as string;

        const token=localStorage.getItem('token');
        if(token){
          const payload = JSON.parse(atob(token.split('.')[1]));
          if (payload.role=="RECRUITER"){
            this.router.navigate(['/recruiter/dashboard']);
          }else if (payload.role=="ADMIN"){
            this.router.navigate(['/admin/admin-dashboard'])
          }else if (payload.role == "CANDIDATE"){
            this.router.navigate(['/jobs'])
          }
        }else {
          this.router.navigate(['/home']);
        }



    },
    error:(err)=> {
        if (err.error.message){
          this.errorMsg=err.error.message;
          this.cdr.detectChanges();
        }else {
        this.errorMsg="Invalid email or password";
        this.cdr.detectChanges();
        }
    }


  });
    }
  }


  forgotPassword(){
    this.router.navigate(['/verif-email'])

  }
  signUp(){
    this.router.navigate(['/register'])
  }



}

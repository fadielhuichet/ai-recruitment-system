import {Component, OnInit} from '@angular/core';
import { ChangePasswordRequest } from '../../models/Dto/Auth/changePasswordRequest';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import { AuthenticationService } from '../../services/services/Authentication-service/authentication-service';
import { CommonModule } from '@angular/common';
import {FormsModule, NgForm} from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-change-password',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './change-password.html',
  styleUrl: './change-password.css',
})
export class ChangePassword implements OnInit{
  changePasswordRequest:ChangePasswordRequest={email:'',codeEmail:'',newPassword:''}
  confirmPassword:String|null=null;
  errorMsg:String|null=null;

  constructor(private router:Router, private  authService:AuthenticationService, private route:ActivatedRoute){}
  ngOnInit() {
    this.route.queryParams.subscribe(params=>{
      this.changePasswordRequest.email=params['email'];
      this.changePasswordRequest.codeEmail=params['codeEmail'];
      console.log(this.changePasswordRequest.email);
    })
  }


  protected onSubmit(changePasswordFrom:NgForm) {
    if (changePasswordFrom.valid){
       if(this.changePasswordRequest.newPassword==this.confirmPassword){
         this.authService.changePassword(this.changePasswordRequest).subscribe({
           next:()=>{
             Swal.fire({
               icon: 'success',
               title: 'Password changed',
               text: 'Redirecting to the login page',
               timer: 2000,
               showConfirmButton: false,
             })
             this.router.navigate(['/login'])
           }
         })
    }

  }
  }
}

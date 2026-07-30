import {Component, OnInit} from '@angular/core';
import { ChangePasswordRequest } from '../../models/Dto/Auth/changePasswordRequest';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../../services/services/Authentication-service/authentication-service';
import {FormsModule, NgForm} from '@angular/forms';
import { CommonModule } from '@angular/common';
import {pattern} from '@angular/forms/signals';

@Component({
  selector: 'app-verif-code',
  imports: [FormsModule, CommonModule],
  templateUrl: './verif-code.html',
  styleUrl: './verif-code.css',
})
export class VerifCode implements OnInit{
  changePasswordRequest:ChangePasswordRequest={email:'',codeEmail:'',newPassword:''};
  errorMsg:String|null=null;
  notifMsg:String|null=null;

  constructor(public router:Router,private authService:AuthenticationService ,private route:ActivatedRoute){}

  ngOnInit() {
    this.route.queryParams.subscribe(params=>{
      this.changePasswordRequest.email=params['email'];
      console.log(this.changePasswordRequest.email);
    })
  }

  resendCode(){
    this.authService.verifEmail(this.changePasswordRequest).subscribe({
      next:()=>{
        this.notifMsg='Code successfully sent.'
      }
    })
  }

  protected onSubmit(verifyCodeForm: NgForm) {
    if(verifyCodeForm.valid){
      this.authService.verifCode(this.changePasswordRequest).subscribe({
        next:()=>{
          this.router.navigate(['/change-password'],{
            queryParams:{email:this.changePasswordRequest.email,codeEmail:this.changePasswordRequest.codeEmail}
          })
        }
      })

    }

  }

  protected readonly pattern = pattern;
}

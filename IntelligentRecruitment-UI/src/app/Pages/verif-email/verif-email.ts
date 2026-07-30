import {ChangeDetectorRef, Component} from '@angular/core';
import { ChangePasswordRequest } from '../../models/Dto/Auth/changePasswordRequest';
import {FormsModule, NgForm, NgModel} from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { AuthenticationService } from '../../services/services/Authentication-service/authentication-service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-verif-email',
  imports: [FormsModule, RouterLink, NgIf],
  templateUrl: './verif-email.html',
  styleUrl: './verif-email.css',
})
export class VerifEmail {
  changePasswordRequest:ChangePasswordRequest={email:'',codeEmail:'',newPassword:''}
  errorMsg:String|null=null;
  isLoading = false;

  constructor(private router:Router,private authService:AuthenticationService,private cdr:ChangeDetectorRef){}


  protected onSubmit(verifEmailForm: NgForm) {
    this.isLoading = true;

    this.authService.verifEmail(this.changePasswordRequest).subscribe({
      next: () => {
        this.router.navigate(['/verif-code'], {
          queryParams: { email: this.changePasswordRequest.email }
        });
      },
      error: (err) => {
        this.isLoading = false;

        if (err.error.error == "EMAIL_NOT_FOUND") {
          this.errorMsg = err.error.message;
          this.cdr.detectChanges()
        } else {
          this.errorMsg = "something went wrong";
          this.cdr.detectChanges();
        }
      }
    });
  }
}

import { Component } from '@angular/core';
import {Header} from '../../Shared/header/header';
import {CommonModule, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {AuthenticationService} from '../../services/services/Authentication-service/authentication-service';
import {ContactRequest} from '../../models/Dto/ContactRequest';

@Component({
  selector: 'app-contact',
  imports: [
    Header,
    CommonModule,
    FormsModule
  ],
  templateUrl: './contact.html',
  styleUrl: './contact.css',
})
export class Contact {
  submitSuccess=false;
  submitError=false;
  errorMsg='';
  loading=false;

  contactRequest: ContactRequest = {
    firstName: '',
    email: '',
    subject: '',
    message: '',
  };

  constructor(private authenticationService: AuthenticationService) {}

  protected onSendMessage(): void {
    if (this.loading) {
      return;
    }

    this.submitSuccess = false;
    this.submitError = false;
    this.errorMsg = '';

    if (!this.contactRequest.firstName || !this.contactRequest.email || !this.contactRequest.subject || !this.contactRequest.message) {
      this.submitError = true;
      this.errorMsg = 'Please fill in all required fields.';
      return;
    }

    this.loading = true;

    this.authenticationService.sendContact(this.contactRequest).subscribe({
      next: () => {
        this.loading = false;
        this.submitSuccess = true;

        setTimeout(() => {
          this.submitSuccess = false;
        }, 3000);
        this.contactRequest = {
          firstName: '',
          email: '',
          subject: '',
          message: '',
        };
      },
      error: (error) => {
        this.loading = false;
        this.submitError = true;
        this.errorMsg = error?.error?.message || 'Please try again later.';
      }
    });
  }
}

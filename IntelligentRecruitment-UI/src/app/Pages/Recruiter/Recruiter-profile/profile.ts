import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Header } from '../../../Shared/header/header';
import { AuthenticationService } from '../../../services/services/Authentication-service/authentication-service';
import { RecruiterService } from '../../../services/services/recruiter-services/recruiter-service';

@Component({
  selector: 'app-profile',
  imports: [
    CommonModule,
    Header,
    FormsModule
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  recruiter = {
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    companyName: ''
  };

  activeSection: 'info' | 'security' = 'info';

  profileImageUrl: string | null = null;
  localPreviewUrl: string | null = null;
  uploading = false;
  uploadError: string | null = null;

  successMsg: string | null = null;
  deleteWarning = false;
  savingInfo = false;
  savingPassword = false;

  passwordForm = { current: '', newPass: '', confirm: '' };
  passwordError: string | null = null;

  constructor(
    private authService: AuthenticationService,
    private recruiterService: RecruiterService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  private loadProfile(): void {
    this.authService.getRecruiterInfo().subscribe({
      next: (data) => {
        this.recruiter = {
          firstName: data.firstName,
          lastName: data.lastName,
          email: data.email,
          phone: data.phone,
          companyName: data.companyName
        };
        if (data.profileImage) {
          const timeStamp = new Date().getTime();
          this.profileImageUrl = `${this.recruiterService.getProfileImageUrl(data.profileImage)}?t=${timeStamp}`;
        }
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  get initials(): string {
    const f = this.recruiter.firstName?.[0] ?? '';
    const l = this.recruiter.lastName?.[0] ?? '';
    return (f + l).toUpperCase() || 'R';
  }

  onSaveInfo(form: NgForm): void {
    if (form.invalid) return;
    this.savingInfo = true;
    this.recruiterService.changeRecruiterInfo(this.recruiter).subscribe({
      next: () => {
        this.savingInfo = false;
        this.showSuccess('Profile updated successfully.');
      },
      error: () => {
        this.savingInfo = false;
      }
    });
  }

  resetInfo(): void {
    this.loadProfile();
  }

  onChangePassword(form: NgForm): void {
    if (form.invalid || this.passwordForm.newPass !== this.passwordForm.confirm) return;
    this.savingPassword = true;
    this.passwordError = null;

    this.recruiterService.changePassword({
      currentPassword: this.passwordForm.current,
      newPassword: this.passwordForm.newPass
    }).subscribe({
      next: () => {
        this.savingPassword = false;
        this.resetPassword();
        this.showSuccess('Password updated successfully.');
      },
      error: (err: any) => {
        this.savingPassword = false;
        this.passwordError = err.error?.message ?? 'Incorrect current password.';
      }
    });
  }

  resetPassword(): void {
    this.passwordForm = { current: '', newPass: '', confirm: '' };
    this.passwordError = null;
  }

  onImageSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    this.localPreviewUrl = URL.createObjectURL(file);
    this.uploading = true;
    this.uploadError = null;

    this.recruiterService.uploadProfileImage(file).subscribe({
      next: (path) => {
        const timeStamp = new Date().getTime();
        this.profileImageUrl = `${this.recruiterService.getProfileImageUrl(path)}?t=${timeStamp}`;
        this.uploading = false;
        this.localPreviewUrl = null;
        this.showSuccess('Photo updated successfully.');
        this.cdr.detectChanges();
      },
      error: () => {
        this.uploading = false;
        this.uploadError = 'Failed to upload image.';
        this.localPreviewUrl = null;
        this.cdr.detectChanges();
      }
    });
  }

  onDeleteAccount(): void {
    this.deleteWarning = true;
    if (!confirm('Are you sure? This will permanently delete your account and cannot be undone.')) {
      this.deleteWarning = false;
      return;
    }
    this.recruiterService.deleteAccount().subscribe({
      next: () => {
        this.deleteWarning = false;
        this.showSuccess('Account deleted successfully.');
      },
      error: () => {
        this.deleteWarning = false;
      }
    });
  }

  private showSuccess(message: string): void {
    this.successMsg = message;
    setTimeout(() => (this.successMsg = null), 4000);
  }
}

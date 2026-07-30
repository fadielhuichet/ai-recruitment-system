import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Header } from '../../../Shared/header/header';
import { AdminService } from '../../../services/services/Admin-service/admin-service';
import { AdminRequest } from '../../../models/Dto/Admin/AdminRequest';

@Component({
  selector: 'app-admin-profile',
  imports: [
    CommonModule,
    FormsModule,
    Header
  ],
  templateUrl: './admin-profile.html',
  styleUrl: './admin-profile.css',
})
export class AdminProfile implements OnInit {
  admin: AdminRequest = { username: '', email: '', phone: '' };
  activeSection: 'info' | 'security' = 'info';

  successMsg: string | null = null;
  savingInfo = false;
  savingPassword = false;

  passwordForm = { current: '', newPass: '', confirm: '' };
  passwordError: string | null = null;

  constructor(private adminService: AdminService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadAdminInfo();
  }

  get initials(): string {
    const u = this.admin.username?.[0] ?? '';

    return (u).toUpperCase() || 'AD';
  }

  loadAdminInfo(): void {
    this.adminService.getAdminInfo().subscribe({
      next: (data) => {
        this.admin = {
          username: data.username,
          email: data.email,
          phone: data.phone
        };
        this.cdr.detectChanges();
      }
    });
  }

  onSaveInfo(form: NgForm): void {
    if (form.invalid) return;
    this.savingInfo = true;
    this.adminService.updateProfile(this.admin).subscribe({
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
    this.loadAdminInfo();
  }

  onChangePassword(form: NgForm): void {
    if (form.invalid || this.passwordForm.newPass !== this.passwordForm.confirm) return;
    this.savingPassword = true;
    this.passwordError = null;

    this.adminService.changePassword({
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

  private showSuccess(message: string): void {
    this.successMsg = message;
    setTimeout(() => (this.successMsg = null), 4000);
  }
}

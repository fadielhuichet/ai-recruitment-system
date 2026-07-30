import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Header } from '../../../Shared/header/header';
import { COUNTRIES } from '../../../models/Enum/Countries';
import { CandidateService } from '../../../services/services/Candidate-service/candidate-service';
import {AuthenticationService} from '../../../services/services/Authentication-service/authentication-service';

@Component({
  selector: 'app-candidate-profile',
  imports: [CommonModule, FormsModule, RouterModule, Header],
  templateUrl: './candidate-profile.html',
  styleUrl: './candidate-profile.css',
})
export class CandidateProfile implements OnInit {

  protected readonly COUNTRIES = COUNTRIES;

  // ── Profile data (pre-filled from API) ────────────────────────────────────
  candidate = {
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    dateOfBirth: '',
    country: '',
  };

  activeSection: 'info' | 'security' | 'cv' = 'info';

  /** Name of the CV already on the server (shown in the pill) */
  currentCvName: string | null = null;

  // ── Avatar initials ────────────────────────────────────────────────────────
  get initials(): string {
    const f = this.candidate.firstName?.[0] ?? '';
    const l = this.candidate.lastName?.[0] ?? '';
    return (f + l).toUpperCase();
  }

  // ── UI state ───────────────────────────────────────────────────────────────
  successMsg: string | null = null;
  deleteWarning = false;
  savingInfo = false;
  savingPassword = false;
  savingCv = false;

  // ── Password form ──────────────────────────────────────────────────────────
  passwordForm = { current: '', newPass: '', confirm: '' };
  passwordError: string | null = null;

  // ── CV ─────────────────────────────────────────────────────────────────────
  newCv: File | null = null;
  cvDragOver = false;
  cvError: string | null = null;

  constructor(private candidateService: CandidateService,private authService:AuthenticationService,private cdr:ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  // ── Load ───────────────────────────────────────────────────────────────────
  private loadProfile(): void {
    this.authService.getCandidateInfo().subscribe({
      next: (data: any) => {
        this.candidate = {
          firstName:   data.firstName,
          lastName:    data.lastName,
          email:       data.email,
          phone:       data.phone,
          dateOfBirth: data.dateOfBirth,
          country:     data.country,
        };
        this.currentCvName = data.cvFileName ?? null;
        this.cdr.detectChanges();
      },
      error: () => {},
    });
  }

  // ── Save personal info ─────────────────────────────────────────────────────
  onSaveInfo(form: NgForm): void {
    if (form.invalid) return;
    this.savingInfo = true;

    this.candidateService.updateProfile(this.candidate).subscribe({
      next: () => {
        this.savingInfo = false;
        this.showSuccess('Profile updated successfully.');
      },
      error: () => {
        this.savingInfo = false;
      },
    });
  }

  resetInfo(): void {
    this.loadProfile();
  }

  // ── Change password ────────────────────────────────────────────────────────
  onChangePassword(form: NgForm): void {
    if (form.invalid || this.passwordForm.newPass !== this.passwordForm.confirm) return;
    this.savingPassword = true;
    this.passwordError = null;

    this.candidateService.changePassword({
      currentPassword: this.passwordForm.current,
      newPassword:     this.passwordForm.newPass,
    }).subscribe({
      next: () => {
        this.savingPassword = false;
        this.resetPassword();
        this.showSuccess('Password updated successfully.');
      },
      error: (err: any) => {
        this.savingPassword = false;
        this.passwordError = err.error?.message ?? 'Incorrect current password.';
      },
    });
  }

  resetPassword(): void {
    this.passwordForm = { current: '', newPass: '', confirm: '' };
    this.passwordError = null;
  }

  // ── CV upload ──────────────────────────────────────────────────────────────
  onCvSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) this.setCv(input.files[0]);
  }

  onCvDrop(event: DragEvent): void {
    event.preventDefault();
    this.cvDragOver = false;
    const file = event.dataTransfer?.files?.[0];
    if (file) this.setCv(file);
  }

  private setCv(file: File): void {
    const allowed = [
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    ];
    if (!allowed.includes(file.type)) {
      this.cvError = 'Only PDF, DOC, or DOCX files are accepted.';
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      this.cvError = 'File size must not exceed 5 MB.';
      return;
    }
    this.cvError = null;
    this.newCv = file;
  }

  removeNewCv(): void {
    this.newCv = null;
    this.currentCvName = null;
  }

  onSaveCv(): void {
    if (!this.newCv) return;
    this.savingCv = true;

    const formData = new FormData();
    formData.set('cv', this.newCv);

    this.candidateService.updateCv(formData).subscribe({
      next: () => {
        this.savingCv = false;
        this.currentCvName = this.newCv!.name;
        this.newCv = null;
        this.showSuccess('CV uploaded successfully.');
      },
      error: () => {
        this.savingCv = false;
        this.cvError = 'Upload failed. Please try again.';
      },
    });
  }

  // ── Delete account ─────────────────────────────────────────────────────────
  onDeleteAccount(): void {
    this.deleteWarning = true;
    if (!confirm('Are you sure? This will permanently delete your account and cannot be undone.')) {
      this.deleteWarning = false;
      return;
    }
    this.candidateService.deleteAccount().subscribe({
      next: () => {
        this.deleteWarning = false;
        this.showSuccess('Account deleted successfully.');
      },
      error: () => {
        this.deleteWarning = false;
      }
    });
  }

  // ── Toast helper ──────────────────────────────────────────────────────────
  private showSuccess(msg: string): void {
    this.successMsg = msg;
    setTimeout(() => (this.successMsg = null), 4000);
  }
}

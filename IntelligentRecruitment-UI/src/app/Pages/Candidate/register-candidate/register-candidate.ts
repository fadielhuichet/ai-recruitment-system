import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CandidateRegisterRequest } from '../../../models/Dto/Auth/CandidateRegisterRequest';
import { Header } from '../../../Shared/header/header';
import { Router, RouterModule } from '@angular/router';
import { COUNTRIES } from '../../../models/Enum/Countries';
import { CandidateService } from '../../../services/services/Candidate-service/candidate-service';
import { AuthenticationService } from '../../../services/services/Authentication-service/authentication-service';

@Component({
  selector: 'app-register-candidate',
  imports: [CommonModule, FormsModule, RouterModule, Header],
  templateUrl: './register-candidate.html',
  styleUrl: './register-candidate.css',
})
export class RegisterCandidate {

  registerRequest: CandidateRegisterRequest = {
    email: '',
    password: '',
    country: 'Tunisia',
    firstName: '',
    lastName: '',
    phone: '',
    dateOfBirth: '',
    cv: null as any,
  };

  confirmPassword = '';
  msg?: string;
  creating = false;

  /** Drag-over state for the drop zone */
  cvDragOver = false;

  /** Tracks whether the user has interacted with the CV field (for validation hint) */
  cvTouched = false;

  constructor(
    private candidateService: CandidateService,
    private authService: AuthenticationService,
    private router: Router,
  ) {}

  // ── CV helpers ────────────────────────────────────────────────────────────

  onCvSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.setCv(input.files[0]);
    }
    this.cvTouched = true;
  }

  onCvDrop(event: DragEvent): void {
    event.preventDefault();
    this.cvDragOver = false;
    const file = event.dataTransfer?.files?.[0];
    if (file) this.setCv(file);
    this.cvTouched = true;
  }

  removeCV(): void {
    this.registerRequest.cv = null as any;
    this.cvTouched = true;
  }

  private setCv(file: File): void {
    const allowed = ['application/pdf', 'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
    const maxBytes = 5 * 1024 * 1024; // 5 MB

    if (!allowed.includes(file.type)) {
      this.msg = 'Only PDF, DOC, or DOCX files are accepted.';
      return;
    }
    if (file.size > maxBytes) {
      this.msg = 'File size must not exceed 5 MB.';
      return;
    }

    this.msg = undefined;
    this.registerRequest.cv = file;
  }

  // ── Form submit ───────────────────────────────────────────────────────────

  protected onRegister(registerForm: NgForm): void {
    this.cvTouched = true;

    if (registerForm.invalid || !this.registerRequest.cv) return;

    const formData = new FormData();
    formData.set('firstName',   this.registerRequest.firstName);
    formData.set('lastName',    this.registerRequest.lastName);
    formData.set('email',       this.registerRequest.email);
    formData.set('dateOfBirth', this.registerRequest.dateOfBirth);
    formData.set('phone',       this.registerRequest.phone);
    formData.set('password',    this.registerRequest.password);
    formData.set('country',     this.registerRequest.country);
    formData.set('cv',          this.registerRequest.cv);

    this.creating = true;

    this.authService.registerCandidate(formData).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.msg = err.error?.message ?? 'Registration failed. Please try again.';
        this.creating = false;
      },
    });
  }

  protected readonly COUNTRIES = COUNTRIES;
}

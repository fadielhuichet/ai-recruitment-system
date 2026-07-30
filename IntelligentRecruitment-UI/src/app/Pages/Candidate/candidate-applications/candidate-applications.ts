import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ApplicationService } from '../../../services/services/Application-service/application-service';
import { AuthenticationService } from '../../../services/services/Authentication-service/authentication-service';
import { CandidateApplicationResponse } from '../../../models/Dto/Application/CandidateApplicationResponse';
import { ApplicationStatus } from '../../../models/Enum/ApplicationStatus';

@Component({
  selector: 'app-candidate-applications',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './candidate-applications.html',
  styleUrl: './candidate-applications.css',
})
export class CandidateApplications implements OnInit {
  applications: CandidateApplicationResponse[] = [];
  loading = true;

  searchTerm = '';
  statusFilter: 'ALL' | ApplicationStatus = 'ALL';
  statusOptions: Array<'ALL' | ApplicationStatus> = [
    'ALL',
    ApplicationStatus.PENDING,
    ApplicationStatus.ANALYZED,
    ApplicationStatus.ACCEPTED,
    ApplicationStatus.REFUSED
  ];

  skeletonRows = Array.from({ length: 6 });

  constructor(
    private authService: AuthenticationService,
    private applicationService: ApplicationService,
    private cdr:ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.authService.getCandidateInfo().subscribe({
      next: (candidate) => {
        this.loadApplications(candidate.id);
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  get filteredApplications(): CandidateApplicationResponse[] {
    const term = this.searchTerm.trim().toLowerCase();
    return this.applications.filter((app) => {
      const matchesTerm = !term ||
        app.jobTitle.toLowerCase().includes(term) ||
        app.company.toLowerCase().includes(term);
      const matchesStatus = this.statusFilter === 'ALL' || app.status === this.statusFilter;
      return matchesTerm && matchesStatus;
    });
  }

  private loadApplications(candidateId: number): void {
    this.loading = true;
    this.applicationService.getApplicationsByCandidate(candidateId).subscribe({
      next: (data) => {
        this.applications = [...data].sort(
          (a, b) => new Date(b.appliedAt).getTime() - new Date(a.appliedAt).getTime()
        );
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}

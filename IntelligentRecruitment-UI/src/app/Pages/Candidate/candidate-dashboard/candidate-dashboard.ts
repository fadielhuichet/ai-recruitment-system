import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApplicationService } from '../../../services/services/Application-service/application-service';
import { AuthenticationService } from '../../../services/services/Authentication-service/authentication-service';
import { CandidateApplicationResponse } from '../../../models/Dto/Application/CandidateApplicationResponse';
import { CandidateApplicationStatsResponse } from '../../../models/Dto/Application/CandidateApplicationStatsResponse';

@Component({
  selector: 'app-candidate-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './candidate-dashboard.html',
  styleUrl: './candidate-dashboard.css',
})
export class CandidateDashboard implements OnInit {
  candidateName = 'Candidate';
  candidateId: number | null = null;

  stats: CandidateApplicationStatsResponse | null = null;
  recentApplications: CandidateApplicationResponse[] = [];

  loadingStats = true;
  loadingApplications = true;

  constructor(
    private authService: AuthenticationService,
    private applicationService: ApplicationService,
    private cdr:ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.authService.getCandidateInfo().subscribe({
      next: (candidate) => {
        this.candidateName = candidate.firstName || 'Candidate';
        this.candidateId = candidate.id;
        this.loadStats(candidate.id);
        this.loadApplications(candidate.id);
        this.cdr.detectChanges();
      },
      error: () => {
        this.loadingStats = false;
        this.loadingApplications = false;
      }
    });
  }

  private loadStats(candidateId: number): void {
    this.loadingStats = true;
    this.applicationService.getCandidateApplicationStats(candidateId).subscribe({
      next: (data) => {
        this.stats = data;
        this.loadingStats = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loadingStats = false;
      }
    });
  }

  private loadApplications(candidateId: number): void {
    this.loadingApplications = true;
    this.applicationService.getApplicationsByCandidate(candidateId).subscribe({
      next: (data) => {
        this.recentApplications = [...data]
          .sort((a, b) => new Date(b.appliedAt).getTime() - new Date(a.appliedAt).getTime())
          .slice(0, 5);
        this.loadingApplications = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loadingApplications = false;
      }
    });
  }
}

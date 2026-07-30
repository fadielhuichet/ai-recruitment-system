import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Observable, Subscription, catchError, finalize, of} from 'rxjs';
import {ApplicationService} from '../../../services/services/Application-service/application-service';
import {PagedResponse} from '../../../models/Dto/PagedResponse';
import {AdminApplicationStatsResponse} from '../../../models/Dto/Application/AdminApplicationStatsResponse';
import {ApplicationStatus} from '../../../models/Enum/ApplicationStatus';
import {AdminApplicationResponse} from '../../../models/Dto/Application/AdminApplicationResponse';
import {MatSelectModule} from '@angular/material/select';
import {MatFormFieldModule} from '@angular/material/form-field';


@Component({
  selector: 'app-applications',
  standalone: true,
  imports: [CommonModule, FormsModule,
    MatSelectModule,
    MatFormFieldModule],
  templateUrl: './applications.html',
  styleUrl: './applications.css',
})
export class AdminApplications implements OnInit, OnDestroy {
  applicationsResponse: PagedResponse<AdminApplicationResponse> | null = null;
  stats$!: Observable<AdminApplicationStatsResponse>;
  loading = true;
  selectedCandidate: AdminApplicationResponse | null = null;
  selectedJob: AdminApplicationResponse | null = null;

  page = 0;
  size = 10;
  searchTerm = '';
  currentFilter: ApplicationStatus | 'ALL' = 'ALL';

  private readonly subscriptions = new Subscription();

  constructor(private applicationService: ApplicationService,private cdr:ChangeDetectorRef) {}

  ngOnInit() {
    this.applicationsResponse = this.buildEmptyResponse();
    this.stats$ = this.applicationService.getAdminApplicationStats();
    this.cdr.detectChanges();
    this.fetchApplications();
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  changePage(newPage: number) {
    this.page = newPage;
    this.fetchApplications();
  }

  onSearch(term: string) {
    this.searchTerm = term;
    this.page = 0;
    this.fetchApplications();
  }

  filterByStatus(value: string) {
    this.currentFilter = value as ApplicationStatus | 'ALL';
    this.page = 0;
    this.fetchApplications();
  }

  downloadCv(applicationId: number) {
    this.applicationService.getCvDownloadUrl(applicationId)
      .subscribe(blob => {
        const fileUrl = window.URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = fileUrl;
        a.download = 'cv.pdf';
        a.click();

        window.URL.revokeObjectURL(fileUrl);
      });
  }

  openCandidateDrawer(application: AdminApplicationResponse) {
    this.selectedCandidate = application;
    this.selectedJob = null;
  }

  closeCandidateDrawer() {
    this.selectedCandidate = null;
  }

  openJobDrawer(application: AdminApplicationResponse) {
    this.selectedJob = application;
    this.selectedCandidate = null;
  }

  closeJobDrawer() {
    this.selectedJob = null;
  }

  protected readonly ApplicationStatus = ApplicationStatus;

  private fetchApplications() {
    this.loading = true;
    const statusFilter = this.currentFilter === 'ALL' ? null : this.currentFilter;
    const hasQuery = this.searchTerm.trim().length > 0;

    const request$ = hasQuery || statusFilter
      ? this.applicationService.searchAdminApplications(this.searchTerm, statusFilter, this.page, this.size)
      : this.applicationService.getAdminApplications(this.page, this.size);

    const sub = request$
      .pipe(
        catchError(() => of(this.buildEmptyResponse())),
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe(response => {
        this.applicationsResponse = response;
      });

    this.subscriptions.add(sub);
  }

  private buildEmptyResponse(): PagedResponse<AdminApplicationResponse> {
    return {
      content: [],
      totalElements: 0,
      totalPages: 1,
      number: this.page,
      size: this.size,
      first: this.page === 0,
      last: true,
    };
  }
}

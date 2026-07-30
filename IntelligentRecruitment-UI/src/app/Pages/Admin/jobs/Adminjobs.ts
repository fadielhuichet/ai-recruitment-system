import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import {Observable, Subscription, catchError, finalize, of, count} from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JobResponse } from '../../../models/Dto/Job/JobResponse';
import { PagedResponse } from '../../../models/Dto/PagedResponse';
import { JobService } from '../../../services/services/job-service/job-service';
import { JobStatsResponse } from '../../../models/Dto/Job/JobStatsResponse';
import {Status} from '../../../models/Dto/Recruiter/Status';
import {JobStatus} from '../../../models/Enum/JobStatus';
import {ApplicationService} from '../../../services/services/Application-service/application-service';

@Component({
  selector: 'app-admin-jobs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './AdminJobs.html',
  styleUrl: './jobs.css',
})
export class AdminJobs implements OnInit, OnDestroy {
  jobsResponse: PagedResponse<JobResponse> | null = null;
  stats$!: Observable<JobStatsResponse>;
  loading = true;

  page = 0;
  size = 10;
  searchTerm = '';
  recruiterTerm ='';
  selectedStatus = '';
  selectedRecruiterFilter = '';
  applicantCounts: { [jobId: number]: number } = {};

  isDeleteModalOpen = false;
  jobToDeleteId: number | null = null;

  selectedJob: JobResponse | null = null;
  selectedRecruiterId: string | null = null;
  selectedRecruiter: {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    company: string;
  } | null = null;

  private readonly subscriptions = new Subscription();
  private jobsRequestSub?: Subscription;

  constructor(private jobService: JobService, private cdr: ChangeDetectorRef,public applicationService:ApplicationService) {}

  ngOnInit() {
    this.jobsResponse = this.buildEmptyResponse();
    this.fetchStats();
    this.fetchJobs();
  }

  fetchStats(){
    this.stats$=this.jobService.getStats();
  }

  ngOnDestroy() {
    this.jobsRequestSub?.unsubscribe();
    this.subscriptions.unsubscribe();
  }

  onSearch(term: string) {
    this.searchTerm = term;
    this.page = 0;
    this.fetchJobs();
  }
  protected onSearchRecruiter(term: string) {
    this.recruiterTerm = term;
    this.selectedRecruiterFilter = term;
    this.page = 0;
    this.fetchJobs();

  }

  onFilterChange(_event: Event) {
    this.page = 0;
    this.fetchJobs();
  }

  changePage(newPage: number) {
    this.page = newPage;
    this.fetchJobs();
  }

  openJobDrawer(job: JobResponse) {
    this.selectedRecruiterId = null;
    this.selectedRecruiter = null;
    this.selectedJob = job;
  }

  openRecruiterDrawer(job: JobResponse, event: Event) {
    event.stopPropagation();
    this.selectedJob = null;
    this.selectedRecruiterId = String(job.recruiterId);
    this.selectedRecruiter = {
      firstName: job.recruiterFirstName,
      lastName: job.recruiterLastName,
      email: job.recruiterEmail,
      phone: job.recruiterPhone,
      company: job.company,
    };
  }
  clearFilters() {
    this.searchTerm = '';
    this.recruiterTerm = '';
    this.selectedStatus = '';
    this.selectedRecruiterFilter = '';
    this.page = 0;
    this.fetchJobs();
  }

  closeDrawers() {
    this.selectedJob = null;
    this.selectedRecruiterId = null;
    this.selectedRecruiter = null;
  }

  private fetchJobs() {
    this.loading = true;
    this.jobsRequestSub?.unsubscribe();
    this.jobsRequestSub = this.jobService
      .getAdminJobs(
        this.page,
        this.size,
        this.selectedStatus,
        this.recruiterTerm,
        this.searchTerm
      )
      .pipe(
        catchError(err => {
          console.error('jobs error:', err?.status, err?.error);
          return of(this.buildEmptyResponse());
        }),
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe(response => {
        this.jobsResponse = response;
        response.content.forEach(job=>{
          this.applicationService.getApplicantCount(job.id)
            .subscribe(count=>{
              this.applicantCounts[job.id]=count;
              this.cdr.detectChanges();
            })
        })
        console.log('admin jobs count:', response?.content?.length ?? 0);
      });
  }

  private buildEmptyResponse(): PagedResponse<JobResponse> {
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
  activateJob(jobId: number) {

      this.jobService.activateJobByAdmin(jobId).subscribe({
        next:()=>{
          console.log('Activating Job:', jobId);
          this.fetchJobs();
          this.fetchStats();
        }
      })

  }

  suspendJob(jobId: number) {

      this.jobService.closeJobByAdmin(jobId).subscribe({
         next:()=>{
           console.log('closing Job:', jobId);
           this.fetchJobs();
           this.fetchStats();
         }
      })


  }

  // --- Delete Modal State ---


  openDeleteConfirm(jobId: number) {
    this.jobToDeleteId = jobId;
    this.isDeleteModalOpen = true;
  }

  closeDeleteConfirm() {
    this.isDeleteModalOpen = false;
    this.jobToDeleteId = null;
  }

  confirmDelete() {
    if (this.jobToDeleteId !== null) {
      this.jobService.deleteJobByAdmin(this.jobToDeleteId).subscribe({
        next:()=>{
          console.log('Confirmed Deleting Job:', this.jobToDeleteId);
          this.fetchJobs();
          this.fetchStats();

        }
      })

      // Close the modal and refresh the list
      this.closeDeleteConfirm();
    }
  }



}

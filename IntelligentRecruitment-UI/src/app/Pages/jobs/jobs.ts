import {ChangeDetectorRef, Component, NgIterable, OnInit} from '@angular/core';
import { Header } from '../../Shared/header/header';
import { CommonModule } from '@angular/common';
import { JobCategory } from '../../models/Dto/Job/jobCategory';
import { JobService } from '../../services/services/job-service/job-service';
import { forkJoin, map, Observable } from 'rxjs';
import { JobResponse } from '../../models/Dto/Job/JobResponse';
import { ApplicationService } from '../../services/services/Application-service/application-service';
import { PagedResponse } from '../../models/Dto/PagedResponse';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {JobStatus} from '../../models/Enum/JobStatus';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';

@Component({
  selector: 'app-jobs',
  imports: [Header, CommonModule, FormsModule],
  templateUrl: './jobs.html',
  styleUrls: ['./jobs.css'],
})
export class Jobs implements OnInit {
  protected readonly Math = Math;

  openJobs$?: Observable<number>;
  categories$!: Observable<JobCategory[]>;
  categoryGroups: { groupLabel: string; categories: JobCategory[] }[] = [];
  selectedCategory: JobCategory | null = null;

  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  jobs: JobResponse[] = [];
  applicantCounts: Record<number, number> = {};
  loading = false;

  protected filterChips: (NgIterable<unknown> & NgIterable<any>) | undefined | null;
  searchTitle = '';
  searchLocation = '';

  safeDescription!: SafeHtml;
  constructor(
    private jobService: JobService,
    private applicationService: ApplicationService,
    private router:Router,
    private cdr:ChangeDetectorRef,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {

    this.openJobs$ = this.jobService.countActiveJobs();
    this.categories$ = this.jobService.loadCategory();

    // Build category groups
    this.categories$.subscribe(cats => {
      const groupMap = new Map<string, JobCategory[]>();
      cats.forEach(cat => {
        if (!groupMap.has(cat.group)) groupMap.set(cat.group, []);
        groupMap.get(cat.group)!.push(cat);
      });
      this.categoryGroups = Array.from(groupMap.entries()).map(([groupLabel, categories]) => ({
        groupLabel,
        categories
      }));
    });

    this.loadJobs();
  }


  getSafeHtml(html: string): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

  private loadJobs() {
    this.loading = true;
    this.jobs = []; // clear previous results immediately

    const request$: Observable<PagedResponse<JobResponse>> = this.selectedCategory
      ? this.jobService.getJobsByCategory(
        this.selectedCategory.value,
        this.currentPage,
        this.pageSize
      )
      : this.jobService.getAllJobsByCreationDateDesc(this.currentPage, this.pageSize);

    request$.subscribe({
      next: (page: PagedResponse<JobResponse>) => {
        console.log('API response:', page);
        this.jobs = page.content;
        this.totalPages = page.totalPages;
        this.totalElements = page.totalElements;
        this.loading = false;
        this.loadApplicantCounts();
        this.cdr.detectChanges()
      },
      error: (err) => {
        console.error('Failed to load jobs:', err);
        this.loading = false;
        this.jobs = [];
      }
    });

  }


  private loadApplicantCounts() {
    if (!this.jobs.length) {
      this.applicantCounts = {};
      return;
    }
    forkJoin(
      this.jobs.map(job =>
        this.applicationService.getApplicantCount(job.id).pipe(
          map(count => ({ id: job.id, count }))
        )
      )
    ).subscribe({
      next: (results) => {
        this.applicantCounts = results.reduce((acc, cur) => {
          acc[cur.id] = cur.count;
          return acc;
        }, {} as Record<number, number>);
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to load applicant counts:', err)
    });
  }

  selectCategory(cat: JobCategory) {
    // Toggle off
    if (this.selectedCategory?.value === cat.value) {
      this.selectedCategory = null;
    } else {
      this.selectedCategory = cat;
    }
    this.currentPage = 0;
    this.loadJobs();
  }

  goToPage(page: number) {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.loadJobs();
  }

  get pages(): number[] {
    const maxVisible = 5;
    let start = Math.max(0, this.currentPage - Math.floor(maxVisible / 2));
    let end = start + maxVisible;
    if (end > this.totalPages) {
      end = this.totalPages;
      start = Math.max(0, end - maxVisible);
    }
    return Array.from({ length: end - start }, (_, i) => start + i);
  }

  onSearch() {
    this.currentPage = 0;
    this.selectedCategory = null;
    this.loading = true;
    this.jobs = [];

    const hasFilters = !!this.searchTitle?.trim() || !!this.searchLocation?.trim();

    if (!hasFilters) {
      this.loadJobs();
      return;
    }

    this.jobService.searchJobs(
      this.searchTitle,
      this.searchLocation,
      '',
      this.currentPage,
      this.pageSize
    ).subscribe({
      next: (page: PagedResponse<JobResponse>) => {
        this.jobs = page.content;
        this.totalPages = page.totalPages;
        this.totalElements = page.totalElements;
        this.loading = false;
        this.loadApplicantCounts(); // ✅ existing method
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to search jobs:', err);
        this.loading = false;
        this.jobs = [];
        this.applicantCounts = {};
      }
    });
  }




  protected selectChip(_chip: any) {}


  protected viewJobDetails(job: JobResponse) {
    this.router.navigate(['/job-details',job.id]);
  }

  protected readonly JobStatus = JobStatus;
}

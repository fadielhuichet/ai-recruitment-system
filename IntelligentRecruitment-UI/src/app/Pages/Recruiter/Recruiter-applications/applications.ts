import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {JobResponse} from '../../../models/Dto/Job/JobResponse';
import {JobService} from '../../../services/services/job-service/job-service';
import {ApplicationService} from '../../../services/services/Application-service/application-service';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {ApplicationResponse} from '../../../models/Dto/Application/ApplicationResponse';
import {ApplicationStatus} from '../../../models/Enum/ApplicationStatus';

@Component({
  selector: 'app-applications',
  imports: [CommonModule],
  templateUrl: './applications.html',
  styleUrl: './applications.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Applications implements OnInit {
  jobs: JobResponse[] = [];
  applicants: ApplicationResponse[] = [];

  applicantsMap: { [key: number]: number } = {};
  selectedJobId: number | null = null;
  selectedApplicantId: number | null = null;
  application?: ApplicationResponse;
  selectedCount: number = 0;
  checkedIds: Set<number> = new Set();
  drawerOpen: boolean = false;
  drawerError: boolean = false;
  showDeleteModal=false;
  deleting=false;
  deleteApplicationId?:number;
  toastMessage: string | null = null;
  toastType: 'success' | 'error' | 'info' = 'info';
  private toastTimer?: ReturnType<typeof setTimeout>;

  constructor(
    private jobService: JobService,
    private applicationService: ApplicationService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.loadJobs();

  }

  loadJobs() {
    this.jobService.recruiterJobsByCreationDate().subscribe((jobs) => {
      this.jobs = jobs;
      this.cdr.detectChanges();

      jobs.forEach((job) => {
        this.applicationService.getApplicantCount(job.id).subscribe((count) => {
          this.applicantsMap[job.id] = count;
          this.cdr.detectChanges();
        });
      });
    });
  }

  selectJob(id: number) {
    this.selectedJobId = id;
    this.applicants = [];
    this.closeDrawer();
    this.clearSelection();
    /*this.loadApplications();*/
    this.OnSortChange('mostRecent');
  }


  selectApplicant(id: number) {
    this.selectedApplicantId = id;
    this.application = undefined;
    this.drawerError = false;
    this.drawerOpen = true;

    this.applicationService.getApplicationById(id).subscribe({
      next: (data) => {
        this.application = data;
        this.cdr.detectChanges();
      },
      error: () => {
        this.drawerError = true;
        this.cdr.detectChanges();
      },
    });
  }

  closeDrawer() {
    this.drawerOpen = false;
    this.selectedApplicantId = null;
    this.application = undefined;
    this.drawerError = false;
  }

  toggleCheck(id: number, checked: boolean) {
    checked ? this.checkedIds.add(id) : this.checkedIds.delete(id);
    this.selectedCount = this.checkedIds.size;
    this.cdr.detectChanges();
  }

  toggleSelectAll(checked: boolean) {
    if (checked) {
      this.applicants.forEach((a) => this.checkedIds.add(a.id));
    } else {
      this.checkedIds.clear();
    }
    this.selectedCount = this.checkedIds.size;
    this.cdr.detectChanges();
  }

  clearSelection() {
    this.checkedIds.clear();
    this.selectedCount = 0;
    this.cdr.detectChanges();
  }

  isChecked(id: number): boolean {
    return this.checkedIds.has(id);
  }

  protected searchCandidate(value: string) {
    if (!this.selectedJobId) return;
    const query = value?.trim();
    if (!query) {
      this.OnSortChange('mostRecent');
      return;
    }
    this.applicationService.searchRecruiterApplications(this.selectedJobId, query).subscribe({
      next: (res) => {
        this.applicants = res.content ?? [];
        this.cdr.detectChanges();
      },
      error: () => {
        this.applicants = [];
        this.cdr.detectChanges();
      }
    });
  }
  protected OnSortChange(value: string) {
    if(!this.selectedJobId) return;
    if(value==='scoreDesc'){
      this.applicationService.loadApplicationByJobByScore(this.selectedJobId).subscribe({
        next:(data)=>{
          this.applicants=data;
          this.cdr.detectChanges();
        }

      })
    }else if (value==='mostRecent') {
      this.applicationService.loadApplicationsByJobByDateDesc(this.selectedJobId).subscribe({
        next: (data) => {
          this.applicants = data;
          this.cdr.detectChanges();
        }
      })
    }else {
      let status: ApplicationStatus | null = null;

      if (value === 'ANALYZED') status = ApplicationStatus.ANALYZED;
      if (value === 'REVIEWED') status = ApplicationStatus.REVIEWED;
      if (value === 'ACCEPTED') status = ApplicationStatus.ACCEPTED;
      if (value === 'REFUSED') status = ApplicationStatus.REFUSED;

      if (status) {
        this.applicationService
          .getApplicationByStatus(this.selectedJobId, status)
          .subscribe({
            next: (res) => {
              this.applicants = res.content;
              this.cdr.detectChanges();
            }
          });
      }
    }

  }


  sendAcceptEmail() {
    this.showToast('Sending acceptance emails...', 'info');
    this.applicationService.bulkAccept(Array.from(this.checkedIds)).subscribe({
      next: () => {
        this.clearSelection();
        this.showToast('Successfully sent acceptance emails!', 'success');
        setTimeout(() => this.refreshApplicants(), 7000); // ← wait for async emails to update status
      },
      error: (err) => {
        console.log('error', err);
        this.showToast('Failed to send acceptance emails.', 'error');
      }
    });
  }

  sendRefusalEmail() {
    this.showToast('Sending refusal emails...', 'info');
    this.applicationService.bulkRefuse(Array.from(this.checkedIds)).subscribe({
      next: () => {
        this.clearSelection();
        this.showToast('Successfully sent refusal emails!', 'success');
        setTimeout(() => this.refreshApplicants(), 1500);
      },
      error: (err) => {
        console.log('error', err);
        this.showToast('Failed to send refusal emails.', 'error');
      }
    });
  }

  sendOneAcceptanceEmail(id: number) {
    this.showToast('Sending acceptance email...', 'info');
    this.applicationService.bulkAccept([id]).subscribe({
      next: () => {
        this.checkedIds.delete(id);
        this.selectedCount = this.checkedIds.size;
        this.closeDrawer();
        this.showToast('Successfully sent acceptance email!', 'success');
        setTimeout(() => this.refreshApplicants(), 1500);
      },
      error: (err) => {
        console.log('error', err);
        this.showToast('Failed to send acceptance email.', 'error');
      }
    });
  }

  sendOneRefusalEmail(id: number) {
    this.showToast('Sending refusal email...', 'info');
    this.applicationService.bulkRefuse([id]).subscribe({
      next: () => {
        this.checkedIds.delete(id);
        this.selectedCount = this.checkedIds.size;
        this.closeDrawer();
        this.showToast('Successfully sent refusal email!', 'success');
        setTimeout(() => this.refreshApplicants(), 1500);
      },
      error: (err) => {
        console.log('error', err);
        this.showToast('Failed to send refusal email.', 'error');
      }
    });
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

  private refreshApplicants() {
    if (!this.selectedJobId) return;
    this.applicationService.loadApplicationsByJobByDateDesc(this.selectedJobId).subscribe({
      next: (data) => {
        this.applicants = data;
        this.cdr.detectChanges();
      }
    });
  }

  protected deleteApplication() {
    this.deleting=true;

    this.applicationService.deleteApplication(this.deleteApplicationId!).subscribe({
      next:()=>{
        this.showDeleteModal=false;
        this.deleting=false;
        this.refreshApplicants();

      },error:()=>{
        this.deleting=false;
      }
    })

  }

  protected showModalDelete(id:number) {
    this.showDeleteModal=true;
    this.deleteApplicationId=id

  }

  protected readonly ApplicationStatus = ApplicationStatus;

  protected updateStatus(id:number,status: ApplicationStatus) {
    this.applicationService.updateStatus(id,status).subscribe({
      next:()=>{
        console.log("status now :",status);
        this.selectApplicant(id);
        this.refreshApplicants();
      }
    })

  }

  private showToast(message: string, type: 'success' | 'error' | 'info', durationMs: number = 2200) {
    this.toastMessage = message;
    this.toastType = type;
    if (this.toastTimer) {
      clearTimeout(this.toastTimer);
    }
    this.cdr.detectChanges();
    this.toastTimer = setTimeout(() => {
      this.toastMessage = null;
      this.cdr.detectChanges();
    }, durationMs);
  }
}

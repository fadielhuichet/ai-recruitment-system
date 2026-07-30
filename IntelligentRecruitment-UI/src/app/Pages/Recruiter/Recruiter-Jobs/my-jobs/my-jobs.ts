import {ChangeDetectorRef, Component, HostListener, OnInit} from '@angular/core';
import {RecruiterLayout} from '../../../../Shared/SideBars/recruiter-layout/recruiter-layout';
import {AsyncPipe, DatePipe, CommonModule} from '@angular/common';
import {JobResponse} from '../../../../models/Dto/Job/JobResponse';
import {JobService} from '../../../../services/services/job-service/job-service';
import {Router, RouterOutlet} from '@angular/router';
import {Observable} from 'rxjs';
import {ApplicationService} from '../../../../services/services/Application-service/application-service';

@Component({
  selector: 'app-my-jobs',
  imports: [
    RecruiterLayout,
    CommonModule,
    DatePipe,
    AsyncPipe,
    RouterOutlet
  ],
  templateUrl: './my-jobs.html',
  styleUrl: './my-jobs.css',
})
export class MyJobs implements OnInit{
  jobs$: Observable<JobResponse[]> | null=null;
  applicantsMap:{[key:number]:number}={};
  openedMenuId: number | string | null = null;

  showDeleteModal = false;
  deleting = false;
  deleteJobId?: number;
  @HostListener('document:click')
  onDocumentClick() {
    this.openedMenuId = null;
  }

  constructor(
    private jobService:JobService ,
    private router:Router,
    private applicationService:ApplicationService,
    private cdr:ChangeDetectorRef) {
  }
  ngOnInit() {
    this.loadJobs();

  }

  loadJobs(){
    this.jobs$=this.jobService.recruiterJobsByCreationDate()
    this.jobs$.subscribe(jobs=>{
      jobs.forEach(job=>{
        this.applicationService.getApplicantCount(job.id)
          .subscribe(count=>{
            this.applicantsMap[job.id]=count
          })
      })
    })
  }




  protected viewDetails(job: JobResponse) {
    this.router.navigate(['recruiter/job-details/',job.id])

  }

  protected toPostJob() {
    this.router.navigate(['/recruiter/post-job'])
  }

  deleteJob(id:number) {
    return this.jobService.deleteJob(id).subscribe({
      next:()=>{
        this.jobs$=this.jobService.recruiterJobsByCreationDate();
        delete this.applicantsMap[id];
      },
      error:(err)=>{
        console.error(err);
      }
    })
  }



  protected closeJob(id: number) {
    this.jobService.closeJob(id).subscribe({
      next:()=>{
        console.log("Job closed")
        this.loadJobs();
        this.cdr.detectChanges();
      }
    })

  }

  protected activateJob(id: number) {
    this.jobService.activateJob(id).subscribe({
      next:()=>{
        console.log("Job activated")
        this.loadJobs();
        this.cdr.detectChanges();
      }
    })


  }
  openDeleteModal(id: number) {
    this.showDeleteModal = true;
    this.deleteJobId = id;
    this.deleting = false;
  }

  confirmDeleteJob() {
    if (!this.deleteJobId) return;

    this.deleting = true;

    this.jobService.deleteJob(this.deleteJobId).subscribe({
      next: () => {
        this.showDeleteModal = false;
        this.deleting = false;

        // refresh
        this.loadJobs();
        delete this.applicantsMap[this.deleteJobId!];
      },
      error: () => {
        this.deleting = false;
      }
    });
  }
}

import {ChangeDetectorRef, Component, OnInit, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {JobResponse} from '../../../../models/Dto/Job/JobResponse';
import {JobService} from '../../../../services/services/job-service/job-service';
import {DatePipe, CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {JobRequest} from '../../../../models/Dto/Job/JobRequest';
import {JobCategory} from '../../../../models/Dto/Job/jobCategory';
import {Observable} from 'rxjs';
import {ApplicationService} from '../../../../services/services/Application-service/application-service';
import {RichTextEditorComponent} from '../post-job/RichTextEditorComponent';

@Component({
  selector: 'app-recruiter-job-details',
  encapsulation: ViewEncapsulation.None,
  imports: [
    CommonModule,
    FormsModule,
    DatePipe,
    RouterLink,
    RichTextEditorComponent
  ],
  templateUrl: './recruiter-job-details.html',
  styleUrl: './recruiter-job-details.css',
})
export class RecruiterJobDetails implements OnInit{
  job?:JobResponse;
  jobCategory$?:Observable<JobCategory[]>;
  submitting=false;
  deleting=false;
  showModifyModal: boolean = false;
  showDeleteModal: boolean = false;
  modifiedJob: JobRequest={
    title: '',
    description: '',
    company: '',
    location: '',
    category: null as any,
    customCategory: ''
  };

  applicants?: number;
  showCloseModal = false;
  showActivateModal = false;



  constructor(private router:Router,private route:ActivatedRoute,private jobService:JobService,private cdr:ChangeDetectorRef,private applicationService:ApplicationService) {
  }

  ngOnInit() {
    const id= this.route.snapshot.paramMap.get('id');
    console.log(id);
    if(id) {
      this.fetchJob();
      this.applicationService.getApplicantCount(+id).subscribe(count => {
        this.applicants = count;
      });
    }

    this.loadJobCategory();
  }
  fetchJob(){
    const id= this.route.snapshot.paramMap.get('id');
    console.log(id);
    if(id) {
      this.jobService.getJobById(+id).subscribe({
        next: (data) => {
          this.job = data;
          this.cdr.detectChanges();
        }
      })
    }

  }


  protected deleteJob(id:number) {
    this.deleting=true;
    this.jobService.deleteJob(id).subscribe({
      next:()=>{
        this.deleting=false;
        this.router.navigate(['/recruiter/jobs'])
    },
      error:(err)=>{
        console.log(err);
      }
    })

  }
  initModifyForm() {
    // Create a copy of the job so changes aren't applied until "Save" is clicked
    if (!this.job) return;

    const { title, description, company, location, category } = this.job;

    this.modifiedJob = {
      title,
      description,
      company,
      location,
      category: category as any,
      customCategory: this.job.customCategory
    };
  }

  protected saveModifications(id:number) {
    this.submitting=true;

    const payload: JobRequest = {
      ...this.modifiedJob,
      category: (this.modifiedJob.category?.value ?? this.modifiedJob.category) as unknown as JobCategory
    };

    this.jobService.changeJobInfo(payload,id).subscribe({
      next:()=>{
        this.submitting = false;
        this.showModifyModal = false;
        // reload updated job
        this.jobService.getJobById(id).subscribe(data => {
          this.job = data;
          this.cdr.detectChanges();
        });

      },
      error:(err)=>{
        this.submitting=false;
        console.error(err);
    }
    })


  }



  loadJobCategory(){
    this.jobCategory$=this.jobService.loadCategory();
    this.cdr.detectChanges();

  }

  activateJob(jobId: number) {
    this.jobService.activateJob(jobId).subscribe({
      next:()=>{
        console.log("Job closed")
        this.fetchJob();

      }
    })

    this.showActivateModal = false;
  }

  closeJob(jobId: number) {
    this.jobService.closeJob(jobId).subscribe({
      next:()=>{
        console.log("Job closed")
        this.fetchJob();
      }
    })

    this.showCloseModal = false;
  }

}

import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Header} from '../../Shared/header/header';
import {ActivatedRoute, Router} from '@angular/router';
import {JobService} from '../../services/services/job-service/job-service';
import {JobResponse} from '../../models/Dto/Job/JobResponse';
import {CommonModule, DatePipe} from '@angular/common';
import {ApplicationService} from '../../services/services/Application-service/application-service';
import {FormControl, FormGroup, FormsModule, NgForm, ReactiveFormsModule, Validators} from '@angular/forms';
import {ApplicationRequest} from '../../models/Dto/Application/ApplicationRequest';
import {JobStatus} from '../../models/Enum/JobStatus';
import {TokenService} from '../../services/services/token/token-service';

@Component({
  selector: 'app-job-details',

  imports: [
    Header,
    DatePipe,
    CommonModule,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './job-details.html',
  styleUrl: './job-details.css',
})
export class JobDetails implements OnInit{
  job?:JobResponse;
  loading=true;
  protected applicantCount: string | undefined;
  showApplyModal=false;

  applicationRequest:ApplicationRequest={candidateFirstName:'',candidateLastName:'',candidateEmail:'',candidatePhone:'',cv:null as any}
  selectedFile: File | null = null;
  selectedFileName: string | null = null;

  cvTouched = false;
  cvSizeError = false;
  cvTypeError = false;

  submitting = false;
  submitSuccess = false;
  submitError = false;
  errorMsg?: string;


  constructor(
    public router:Router,
    private route: ActivatedRoute,
    private jobService: JobService,
    private applicationService:ApplicationService,
    private cdr:ChangeDetectorRef,
    private tokenService:TokenService) {
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    console.log('ID from URL:', id);

    if (id) {
      this.jobService.getJobById(+id).subscribe({
        next: (data) => {
          console.log('Job data received:', data);
          this.job = data;
          this.loading = false;
          this.cdr.detectChanges();

          this.applicationService.getApplicantCount(data.id).subscribe({
            next: (count) =>{
              this.applicantCount = count.toString();
              this.cdr.detectChanges();
            },
            error:() =>{
              this.applicantCount = '0'
              this.cdr.detectChanges();
            }
          });
        },
        error: (err) => {
          console.error('Job fetch error:', err);
          this.loading = false;
          this.cdr.detectChanges()
        }
      });
    } else {
      this.router.navigate(['/jobs']);
    }
  }






  protected onSubmit(job: JobResponse|undefined) {
    if (this.tokenService.hasToken()){
    if (!job) return;


    this.submitting = true;
    this.submitError = false;


    this.applicationService.submitApplication(job.id).subscribe({
      next: () => {
        this.submitting = false;
        this.submitSuccess = true;
        this.cdr.detectChanges();
        // close modal after 2s
        setTimeout(() => {
          this.submitSuccess = false;

          this.selectedFileName = null;
          this.selectedFile = null;
        }, 2000);
      },
      error: (err) => {
        console.error('Submission error:', err);
        this.submitError = true;
        this.errorMsg=err.error.message;
        this.submitting = false;
        this.cdr.detectChanges();
      }
    });
    }else {
      this.router.navigate(['/login'])
    }
  }

  protected readonly JobStatus = JobStatus;
}

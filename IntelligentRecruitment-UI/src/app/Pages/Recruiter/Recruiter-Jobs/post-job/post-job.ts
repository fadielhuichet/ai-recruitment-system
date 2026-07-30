import { Component, OnInit } from '@angular/core';
import { JobRequest } from '../../../../models/Dto/Job/JobRequest';
import { FormsModule, NgForm } from '@angular/forms';
import { JobService } from '../../../../services/services/job-service/job-service';
import { Router, RouterLink } from '@angular/router';
import { JobCategory } from '../../../../models/Dto/Job/jobCategory';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';
import { QuillModule } from 'ngx-quill';
import {RichTextEditorComponent} from './RichTextEditorComponent';

@Component({
  selector: 'app-post-job',
  imports: [
    FormsModule,
    CommonModule,
    RouterLink,
    QuillModule,
    RichTextEditorComponent,
    // ← add this
  ],
  templateUrl: './post-job.html',
  styleUrl: './post-job.css',
})
export class PostJob implements OnInit {
  jobRequest: JobRequest = {
    title: '',
    description: '',
    company: '',
    location: '',
    category: null as any,
    customCategory: ''
  };
  submitting = false;
  categoriesArray$?: Observable<JobCategory[]>;

  // ← Quill toolbar config: only what a recruiter needs
  quillModules = {
    toolbar: [
      [{ header: 2 }],    // "Section Title"
      ['bold'],           // Bold
      ['clean']           // Remove formatting
    ]
  };

  constructor(private jobService: JobService, private router: Router) {}

  ngOnInit() {
    this.loadCategory();
  }

  loadCategory() {
    this.categoriesArray$ = this.jobService.loadCategory();
  }

  protected onSubmit(createJobForm: NgForm) {
    if (createJobForm.valid) {
      this.submitting = true;
      this.jobService.createJob(this.jobRequest).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Job created',
            text: 'Redirecting to My jobs page',
            timer: 2000,
            showConfirmButton: false,
          });
          this.router.navigate(['/recruiter/jobs']);
        },
        error: () => {
          this.submitting = false;
        }
      });
    }
  }
}

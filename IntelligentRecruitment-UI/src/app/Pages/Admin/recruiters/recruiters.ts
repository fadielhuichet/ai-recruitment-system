import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { RecruiterResponse } from '../../../models/Dto/Recruiter/RecruiterResponse';
import { RecruiterService } from '../../../services/services/recruiter-services/recruiter-service';
import {Observable, BehaviorSubject, switchMap, shareReplay} from 'rxjs';
import { PagedResponse } from '../../../models/Dto/PagedResponse';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {Status} from '../../../models/Dto/Recruiter/Status';
import {RecruiterStatsResponse} from '../../../models/Dto/Recruiter/RecruiterStatsResponse';

@Component({
  selector: 'app-recruiters',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recruiters.html',
  styleUrl: './recruiters.css',
})
export class Recruiters implements OnInit {
  recruiters$?: Observable<PagedResponse<RecruiterResponse>>;
  private currentFilter = 'ALL';
  stats$!:Observable<RecruiterStatsResponse>;

  page = 0;
  size = 10;
  searchTerm = '';

  // Use a BehaviorSubject to trigger re-fetches easily when page or search changes
  private refreshTrigger = new BehaviorSubject<void>(undefined);
  totalPages?: number;
  totalElements?: number;

  constructor(private recruiterService: RecruiterService,private cdr:ChangeDetectorRef) {}

  ngOnInit() {
    this.loadRecruiters();
    this.fetchStats();
  }

  fetchStats(){
    this.stats$=this.recruiterService.getStats();
  }
  onSearch(term: string) {
    this.searchTerm = term;
    this.page = 0;
    this.refreshTrigger.next();
  }

  changePage(newPage: number) {
    this.page = newPage;
    this.refreshTrigger.next();
  }

  // --- Administrative Actions ---

  activateRecruiter(id: number) {
    this.recruiterService.activateRecruiter(id).subscribe({
      next: (res) => {
        console.log(res);
        this.refreshTrigger.next(); // ← not loadRecruiters()
        this.stats$ = this.recruiterService.getStats(); // ← reassign Observable, async pipe re-subscribes
      },
      error: (err) => console.log(err)
    });
  }

  suspendRecruiter(id: number) {
    this.recruiterService.suspendRecruiter(id).subscribe({
      next: (res) => {
        console.log(res);
        this.refreshTrigger.next();
        this.stats$ = this.recruiterService.getStats();
      },
      error: (err) => console.log(err)
    });
  }

  deleteRecruiter(id: number) {
    if (confirm('Are you sure you want to permanently delete this recruiter? This cannot be undone.')) {
      this.recruiterService.deleteRecruiter(id).subscribe({
        next: () => {
          this.refreshTrigger.next();
          this.stats$ = this.recruiterService.getStats();
        },
        error: (err) => console.log(err)
      });
    }
  }

  protected filterByStatus(value: string) {
    this.currentFilter = value;
    this.page = 0;
    this.refreshTrigger.next();
  }

  loadRecruiters() {
    this.recruiters$ = this.refreshTrigger.pipe(
      switchMap(() => {
        console.log('Reloading recruiters...');

        if (this.searchTerm.trim()) {
          return this.recruiterService.searchRecruiter(
            this.searchTerm,this.page, this.size
          );
        }
        if (this.currentFilter === 'ACTIVE') {
          return this.recruiterService.getActivatedRecruiters(this.page, this.size);
        }
        if (this.currentFilter === 'SUSPENDED') {
          return this.recruiterService.getSuspendedRecruiters(this.page, this.size);
        }
          return this.recruiterService.getRecruiterOrderByCreatedAtDesc(this.page, this.size);

      }),
    shareReplay(1)
  );
  }





  protected readonly Status = Status;

}

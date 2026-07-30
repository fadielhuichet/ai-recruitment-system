import {Component, OnInit} from '@angular/core';
import {AsyncPipe, CommonModule, DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Observable, BehaviorSubject, shareReplay, switchMap} from 'rxjs';
import {CandidateResponse} from '../../../models/Dto/Candidate/CandidateResponse';
import {PagedResponse} from '../../../models/Dto/PagedResponse';
import {CandidateStatsResponse} from '../../../models/Dto/Candidate/CandidateStatsResponse';
import {Status} from '../../../models/Dto/Recruiter/Status';
import {CandidateService} from '../../../services/services/Candidate-service/candidate-service';

@Component({
  selector: 'app-candidates',
  imports: [
    AsyncPipe,
    DatePipe,
    FormsModule,
    CommonModule
  ],
  templateUrl: './candidates.html',
  styleUrl: './candidates.css',
})
export class Candidates implements OnInit{
  candidate$?:Observable<PagedResponse<CandidateResponse>>;
  stats$?:Observable<CandidateStatsResponse>;
  searchTerm='';
  private currentFilter = 'ALL';

  protected readonly Status = Status;
  page=0;
  size=10;

  private refreshTrigger = new BehaviorSubject<void>(undefined);

  constructor(private candidateService:CandidateService) {
  }

  ngOnInit() {
    this.loadCandidates();
    this.fetchStats();
  }

  fetchStats(){
    this.stats$=this.candidateService.getStats();
  }

  loadCandidates(){
    this.candidate$=this.refreshTrigger.pipe(
      switchMap(() => {
        if (this.searchTerm.trim()) {
          return this.candidateService.searchCandidates(this.searchTerm, this.page, this.size);
        }
        if (this.currentFilter === Status.ACTIVE) {
          return this.candidateService.getActivatedCandidates(this.page, this.size);
        }
        if (this.currentFilter === Status.SUSPENDED) {
          return this.candidateService.getSuspendedCandidates(this.page, this.size);
        }
        return this.candidateService.getCandidatesOrderByCreatedAtDesc(this.page, this.size);
      }),
      shareReplay(1)
    );
  }

  protected filterByStatus(value: string) {
    this.currentFilter = value;
    this.page = 0;
    this.refreshTrigger.next();
  }

  protected onSearch(term: string) {
    this.searchTerm = term;
    this.page = 0;
    this.refreshTrigger.next();
  }

  protected changePage(newPage: number) {
    this.page=newPage;
    this.refreshTrigger.next();
  }

  protected activateCandidate(id: number) {
    this.candidateService.activateCandidate(id).subscribe({
      next:()=>{
        this.refreshTrigger.next();
        this.stats$ = this.candidateService.getStats();

      }
      }
    )

  }

  protected suspendCandidate(id: number) {
    this.candidateService.suspendCandidate(id).subscribe({
        next:()=>{
          this.refreshTrigger.next();
          this.stats$ = this.candidateService.getStats();
        }
      }
    )

  }

  protected deleteCandidate(id: number) {
    if (confirm('Are you sure you want to permanently delete this recruiter? This cannot be undone.')) {
      this.candidateService.deleteAccount().subscribe({
          next: () => {
            this.refreshTrigger.next();
            this.stats$ = this.candidateService.getStats();

          }
        }
      )

    }
  }
}

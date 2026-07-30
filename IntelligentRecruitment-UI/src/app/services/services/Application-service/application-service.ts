import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApplicationResponse} from '../../../models/Dto/Application/ApplicationResponse';
import {JobResponse} from '../../../models/Dto/Job/JobResponse';
import {BulkEmailRequest} from '../../../models/Dto/BulkEmailRequest';
import {PagedResponse} from '../../../models/Dto/PagedResponse';
import {ApplicationStatus} from '../../../models/Enum/ApplicationStatus';
import {AdminApplicationStatsResponse} from '../../../models/Dto/Application/AdminApplicationStatsResponse';
import {RecruiterApplicationStatsResponse} from '../../../models/Dto/Application/RecruiterApplicationStatsResponse';
import {CandidateApplicationResponse} from '../../../models/Dto/Application/CandidateApplicationResponse';
import {CandidateApplicationStatsResponse} from '../../../models/Dto/Application/CandidateApplicationStatsResponse';
import {AdminApplicationResponse} from '../../../models/Dto/Application/AdminApplicationResponse';

@Injectable({
  providedIn: 'root',
})
export class ApplicationService {

  private baseUrl="http://localhost:8086/APPLICATION-SERVICE/api/v1"

  constructor(private http:HttpClient) {
  }

  getApplicantCount(id:number):Observable<number>{
    return this.http.get<number>(`${this.baseUrl}/jobs/${id}/applications/count`);

  }

  submitApplication(job_id:number): Observable<any> {
    return this.http.post(`${this.baseUrl}/jobs/${job_id}/applications`,{});
  }

  loadApplicationsByJobByDateDesc(id:number):Observable<ApplicationResponse[]>{
    return this.http.get<ApplicationResponse[]>(`${this.baseUrl}/jobs/${id}/applications?sort=createdAt,desc`);
  }
  loadApplicationByJobByScore(id:number):Observable<ApplicationResponse[]>{
    return this.http.get<ApplicationResponse[]>(`${this.baseUrl}/jobs/${id}/applications?sort=score,desc`)
  }

  getApplicationById(id:number):Observable<ApplicationResponse>{
    return this.http.get<ApplicationResponse>(`${this.baseUrl}/applications/${id}`)
  }

  bulkAccept(applicationIds:number[]):Observable<any>{
    return this.http.post(`${this.baseUrl}/accept`,applicationIds,{responseType: 'text'});
  }
  bulkRefuse(applicationIds:number[]):Observable<any>{
    return this.http.post(`${this.baseUrl}/refuse`,applicationIds,{responseType:'text'});
  }

  deleteApplication(id: number):Observable<any> {
    return this.http.delete(`${this.baseUrl}/applications/${id}`);

  }

  getApplicationByStatus(id:number,status:ApplicationStatus,page=0,size=10):Observable<PagedResponse<ApplicationResponse>>{
    return this.http.get<PagedResponse<ApplicationResponse>>(`${this.baseUrl}/applicationsByStatus?id=${id}&status=${status}&page=${page}&size=${size}`);
  }

  updateStatus(id:number,status:ApplicationStatus):Observable<any>{
    return this.http.patch(`${this.baseUrl}/updateStatus?id=${id}&status=${status}`,{})
  }


  //STATS---------------
  // application.service.ts
  getAdminApplicationStats(): Observable<AdminApplicationStatsResponse> {
    return this.http.get<AdminApplicationStatsResponse>(
      `${this.baseUrl}/stats/admin`
    );
  }

  getAdminApplications(page = 0, size = 10): Observable<PagedResponse<AdminApplicationResponse>> {
    return this.http.get<PagedResponse<AdminApplicationResponse>>(
      `${this.baseUrl}/applications?page=${page}&size=${size}`
    );
  }

  searchAdminApplications(
    query = '',
    status: ApplicationStatus | null = null,
    page = 0,
    size = 10
  ): Observable<PagedResponse<AdminApplicationResponse>> {
    const params = new URLSearchParams();
    if (query.trim()) {
      params.set('query', query.trim());
    }
    if (status) {
      params.set('status', status);
    }
    params.set('page', page.toString());
    params.set('size', size.toString());
    return this.http.get<PagedResponse<AdminApplicationResponse>>(
      `${this.baseUrl}/applications/search?${params.toString()}`
    );
  }

  getRecruiterApplicationStats(): Observable<RecruiterApplicationStatsResponse> {
    return this.http.get<RecruiterApplicationStatsResponse>(
      `${this.baseUrl}/stats/recruiter`
    );
  }

  getApplicationsByCandidate(candidateId: number): Observable<CandidateApplicationResponse[]> {
    return this.http.get<CandidateApplicationResponse[]>(
      `${this.baseUrl}/${candidateId}/applications`
    );
  }

  searchRecruiterApplications(
    jobId: number,
    query = '',
    page = 0,
    size = 50
  ): Observable<PagedResponse<ApplicationResponse>> {
    const params = new URLSearchParams();
    if (query.trim()) {
      params.set('query', query.trim());
    }
    params.set('page', page.toString());
    params.set('size', size.toString());
    return this.http.get<PagedResponse<ApplicationResponse>>(
      `${this.baseUrl}/jobs/${jobId}/applications/search?${params.toString()}`
    );
  }

  getCandidateApplicationStats(candidateId: number): Observable<CandidateApplicationStatsResponse> {
    return this.http.get<CandidateApplicationStatsResponse>(
      `${this.baseUrl}/${candidateId}/stats`
    );
  }

  getCvDownloadUrl(applicationId: number): Observable<any> {
    return this.http.get( `${this.baseUrl}/download/${applicationId}`,{ responseType: 'blob' });
  }
}

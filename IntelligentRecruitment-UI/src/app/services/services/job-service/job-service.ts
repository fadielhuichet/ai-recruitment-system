import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {JobCategory} from '../../../models/Dto/Job/jobCategory';
import {Observable} from 'rxjs';
import {JobResponse} from '../../../models/Dto/Job/JobResponse';
import {PagedResponse} from '../../../models/Dto/PagedResponse';
import {JobRequest} from '../../../models/Dto/Job/JobRequest';
import {JobStatsResponse} from '../../../models/Dto/Job/JobStatsResponse';
import {RecruiterJobStatsResponse} from '../../../models/Dto/Job/RecruiterJobStatsResponse';

@Injectable({
  providedIn: 'root',
})
export class JobService {

  private baseUrl='http://localhost:8086/JOB-SERVICE/api/v1'

  constructor(private http:HttpClient) {
  }


  loadCategory():Observable<JobCategory[]>{
    return this.http.get<JobCategory[]>(this.baseUrl + '/job-categories');
  }

  getAdminJobs(page = 0, size = 10, status = '', recruiterName = '', query = '') {
    const params = new URLSearchParams();
    params.set('page', page.toString());
    params.set('size', size.toString());
    if (status) params.set('status', status);
    if (recruiterName) params.set('recruiterName', recruiterName);
    if (query) params.set('query', query);
    return this.http.get<PagedResponse<JobResponse>>(
      `${this.baseUrl}/jobs/admin?${params.toString()}`
    );
  }
  getStats():Observable<JobStatsResponse>{
    return this.http.get<JobStatsResponse>(`${this.baseUrl}/stats`)
  }

  getAllJobsByCreationDateDesc(page = 0, size = 10):Observable<PagedResponse<JobResponse>>{
    return this.http.get<PagedResponse<JobResponse>>(`${this.baseUrl}/jobsByCreationDateDesc?page=${page}&size=${size}`);
  }
  countActiveJobs():Observable<number>{
    return this.http.get<number>(this.baseUrl + '/activeJobs');
  }
  getJobById(id:number):Observable<JobResponse>{
    return this.http.get<JobResponse>(`${this.baseUrl}/job/${id}`)
  }
  getJobsByCategory(category:string,page = 0, size = 10):Observable<PagedResponse<JobResponse>>{
    return this.http.get<PagedResponse<JobResponse>>(`${this.baseUrl}/jobsByCategory/${category}?page=${page}&size=${size}`);
  }

  searchJobs(
    query = '',
    location = '',
    category = '',
    page = 0,
    size = 10
  ): Observable<PagedResponse<JobResponse>> {
    const params = new URLSearchParams();
    if (query)    params.set('query', query);
    if (location) params.set('location', location);
    if (category) params.set('category', category);
    params.set('page', page.toString());
    params.set('size', size.toString());

    return this.http.get<PagedResponse<JobResponse>>(
      `${this.baseUrl}/search?${params.toString()}`
    );
  }

  recruiterJobsByCreationDate():Observable<JobResponse[]>{
    return this.http.get<JobResponse[]>(`${this.baseUrl}/my-jobs`)
  }
  createJob(request:JobRequest):Observable<any>{
    return this.http.post(`${this.baseUrl}/jobs`,request)
  }
  deleteJob(id:number):Observable<any>{
    return this.http.delete(`${this.baseUrl}/jobs/${id}`);
  }
  changeJobInfo(request:JobRequest,id:number):Observable<any>{
    return this.http.patch(`${this.baseUrl}/jobs/${id}`,request);
  }

  activateJobByAdmin(jobId: number): Observable<void> {
    return this.http.patch<void>(
      `${this.baseUrl}/activateByAdmin/${jobId}`,
      {}
    );
  }

  closeJobByAdmin(jobId: number): Observable<void> {
    return this.http.patch<void>(
      `${this.baseUrl}/closeByAdmin/${jobId}`,
      {}
    );
  }

  deleteJobByAdmin(jobId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/deleteByAdmin/${jobId}`
    );
  }

  activateJob(jobId: number): Observable<string> {
    return this.http.patch(
      `${this.baseUrl}/jobs/${jobId}/active`,
      {},
      { responseType: 'text' }
    );
  }

  closeJob(jobId: number): Observable<string> {
    return this.http.patch(
      `${this.baseUrl}/jobs/${jobId}/close`,
      {},
      { responseType: 'text' }
    );
  }

  deleteRecruiterJob(jobId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/jobs/${jobId}`
    );
  }


  //STATS---------------
  getMyJobStats(): Observable<RecruiterJobStatsResponse> {
    return this.http.get<RecruiterJobStatsResponse>(`${this.baseUrl}/my-job-stats`);
  }

  getLast3Jobs():Observable<JobResponse[]> {
    return this.http.get<JobResponse[]>(`${this.baseUrl}/latest`)

  }
}

import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ChangeRecruiterInfoRequest} from '../../../models/Dto/Recruiter/ChangeRecruiterInfoRequest';
import {RecruiterResponse} from '../../../models/Dto/Recruiter/RecruiterResponse';
import {PagedResponse} from '../../../models/Dto/PagedResponse';
import {RecruiterStatsResponse} from '../../../models/Dto/Recruiter/RecruiterStatsResponse';
import {ChangeRecruiterPasswordRequest} from '../../../models/Dto/Recruiter/ChangeRecruiterPasswordRequest';

@Injectable({
  providedIn: 'root',
})
export class RecruiterService {

  private baseUrl='http://localhost:8086/RECRUITER-SERVICE/recruiter';

  constructor(private http:HttpClient) {
  }

  changeRecruiterInfo(request:ChangeRecruiterInfoRequest):Observable<any>{
    return this.http.patch(`${this.baseUrl}/account-info`,request);
  }

  changePassword(request: ChangeRecruiterPasswordRequest): Observable<any> {
    return this.http.patch(`${this.baseUrl}/changePassword`, request);
  }

  getRecruiterOrderByCreatedAtDesc(page=0,size=10):Observable<PagedResponse<RecruiterResponse>>{
    return this.http.get<PagedResponse<RecruiterResponse>>(`${this.baseUrl}?sort=createdAt,desc&page=${page}&size=${size}`);
  }
  activateRecruiter(recruiterId:number):Observable<any>{
    return this.http.put(`${this.baseUrl}/${recruiterId}/activate`,{});
  }
  suspendRecruiter(recruiterId:number):Observable<any>{
    return this.http.put(`${this.baseUrl}/${recruiterId}/suspend`,{})
  }
  deleteRecruiter(recruiterId:number):Observable<any>{
    return this.http.delete(`${this.baseUrl}/account/${recruiterId}/admin-delete`)
  }

  deleteAccount(): Observable<any> {
    return this.http.delete(`${this.baseUrl}/account/delete`);
  }
  searchRecruiter(
    query='',
    page=0,
    size=10
  ):Observable<PagedResponse<RecruiterResponse>>{
    return this.http.get<PagedResponse<RecruiterResponse>>(`${this.baseUrl}/search?query=${query}&page=${page}&size=${size}`)

  }

  getActivatedRecruiters(page=0,size=10):Observable<PagedResponse<RecruiterResponse>>{
    return this.http.get<PagedResponse<RecruiterResponse>>(`${this.baseUrl}/activatedRecruiters?page=${page}&size=${size}`)
  }
  getSuspendedRecruiters(page=0,size=10):Observable<PagedResponse<RecruiterResponse>>{
    return this.http.get<PagedResponse<RecruiterResponse>>(`${this.baseUrl}/suspendedRecruiters?page=${page}&size=${size}`)
  }
  getStats():Observable<RecruiterStatsResponse>{
    return this.http.get<RecruiterStatsResponse>(`${this.baseUrl}/stats`);
  }


  uploadProfileImage(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(
      `${this.baseUrl}/profile-image`,
      formData,
      { responseType: 'text' }
    );
  }

  getProfileImageUrl(path: string): string {
    return `http://localhost:8086/RECRUITER-SERVICE${path}`;
  }
}

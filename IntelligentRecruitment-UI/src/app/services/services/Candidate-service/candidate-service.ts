import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CandidateResponse } from '../../../models/Dto/Candidate/CandidateResponse';
import { PagedResponse } from '../../../models/Dto/PagedResponse';
import { CandidateStatsResponse } from '../../../models/Dto/Candidate/CandidateStatsResponse';

@Injectable({
  providedIn: 'root',
})
export class CandidateService {
  private baseUrl = 'http://localhost:8086/CANDIDATE-SERVICE/api/v1';

  constructor(private http: HttpClient) {}

  getCandidatesOrderByCreatedAtDesc(
    page = 0,
    size = 10
  ): Observable<PagedResponse<CandidateResponse>> {
    return this.http.get<PagedResponse<CandidateResponse>>(
      `${this.baseUrl}/candidates?sort=createdAt,desc&page=${page}&size=${size}`
    );
  }

  getActivatedCandidates(
    page = 0,
    size = 10
  ): Observable<PagedResponse<CandidateResponse>> {
    return this.http.get<PagedResponse<CandidateResponse>>(
      `${this.baseUrl}/activatedCandidates?page=${page}&size=${size}`
    );
  }

  getSuspendedCandidates(
    page = 0,
    size = 10
  ): Observable<PagedResponse<CandidateResponse>> {
    return this.http.get<PagedResponse<CandidateResponse>>(
      `${this.baseUrl}/suspendedCandidates?page=${page}&size=${size}`
    );
  }

  searchCandidates(
    query = '',
    page = 0,
    size = 10
  ): Observable<PagedResponse<CandidateResponse>> {
    const params = new URLSearchParams();
    if (query) params.set('query', query);
    params.set('page', page.toString());
    params.set('size', size.toString());
    return this.http.get<PagedResponse<CandidateResponse>>(
      `${this.baseUrl}/candidate/search?${params.toString()}`
    );
  }

  findByName(name: string): Observable<CandidateResponse[]> {
    return this.http.get<CandidateResponse[]>(
      `${this.baseUrl}/candidate/search/byName?name=${encodeURIComponent(name)}`
    );
  }

  getStats(): Observable<CandidateStatsResponse> {
    return this.http.get<CandidateStatsResponse>(
      `${this.baseUrl}/candidate/stats`
    );
  }

  suspendCandidate(candidateId: number, reason = ''): Observable<any> {
    const payload = reason ? reason : {};
    return this.http.put(
      `${this.baseUrl}/candidate/${candidateId}/suspend`,
      payload
    );
  }

  activateCandidate(candidateId: number): Observable<any> {
    return this.http.put(
      `${this.baseUrl}/candidate/${candidateId}/activate`,
      {}
    );
  }

  deleteAccount():Observable<any> {
    return this.http.delete(`${this.baseUrl}/account/delete`)

  }

  updateCv(formData: FormData):Observable<any> {
    return this.http.patch(`${this.baseUrl}/updateCv`, formData);

  }

  changePassword(param: { currentPassword: string; newPassword: string }):Observable<any> {
    return this.http.patch(`${this.baseUrl}/changePassword`, param);

  }

  updateProfile(candidate: {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    dateOfBirth: string;
    country: string
  }):Observable<any> {
    return this.http.put(`${this.baseUrl}/updateProfile`, candidate);

  }
}

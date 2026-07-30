import { HttpClient,HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, retry } from 'rxjs';
import { LoginRequest } from '../../../models/Dto/Auth/loginRequest';
import { registerRequest } from '../../../models/Dto/Auth/registerRequest';
import { ChangePasswordRequest } from '../../../models/Dto/Auth/changePasswordRequest';
import {AuthResponse} from '../../../models/Dto/Auth/AuthResponse';
import {AdminResponse} from '../../../models/Dto/Admin/AdminResponse';
import {CandidateRegisterRequest} from '../../../models/Dto/Auth/CandidateRegisterRequest';
import {CandidateResponse} from '../../../models/Dto/Candidate/CandidateResponse';
import {ContactRequest} from '../../../models/Dto/ContactRequest';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {

  private baseUrl='http://localhost:8086/AUTHENTICATION-SERVICE/auth'

  constructor(private http:HttpClient){}

  getRecruiterInfo():Observable<AuthResponse>{
    return this.http.get<AuthResponse>(`${this.baseUrl}/recruiter-info`)
  }
  getAdminInfo():Observable<AdminResponse> {
    return this.http.get<AdminResponse>(`${this.baseUrl}/admin-info`)

  }

  login(request:LoginRequest):Observable<any> {
    return this.http.post(this.baseUrl + '/login', request);
  }
  register(request:registerRequest):Observable<any>{
    return this.http.post(this.baseUrl + '/register',request);
  }
  verifEmail(request:ChangePasswordRequest):Observable<any>{
    return this.http.post(this.baseUrl + '/verifyEmail',request);
  }
  verifCode(request:ChangePasswordRequest):Observable<any>{
    return this.http.post(this.baseUrl + '/verifyCode',request);
  }
  changePassword(request:ChangePasswordRequest):Observable<any>{
    return this.http.post(this.baseUrl + '/changePassword',request);
  }


  registerCandidate(formData:FormData):Observable<any>{
    return this.http.post(this.baseUrl + '/candidateRegister',formData);
  }

  getCandidateInfo():Observable<CandidateResponse>{
    return this.http.get<CandidateResponse>(`${this.baseUrl}/candidate-info`)
  }

  sendContact(request: ContactRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/contact`, request);
  }

}

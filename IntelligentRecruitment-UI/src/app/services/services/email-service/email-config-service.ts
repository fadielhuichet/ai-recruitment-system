import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {EmailConfigDto} from '../../../models/Dto/EmailConfigDto';

@Injectable({
  providedIn: 'root',
})
export class EmailConfigService {
  private baseUrl='http://localhost:8086/RECRUITER-SERVICE/email-config'
  constructor(private http:HttpClient) {
  }

  getMyConfig():Observable<any> {
    return this.http.get(`${this.baseUrl}`)

  }

  updateMyConfig(request: EmailConfigDto):Observable<any> {
    return this.http.put(`${this.baseUrl}`,request);

  }
}

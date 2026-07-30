import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminResponse } from '../../../models/Dto/Admin/AdminResponse';
import { AdminRequest } from '../../../models/Dto/Admin/AdminRequest';
import { ChangeAdminPasswordRequest } from '../../../models/Dto/Admin/ChangeAdminPasswordRequest';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private baseUrl = 'http://localhost:8086/ADMIN-SERVICE/admin';

  constructor(private http: HttpClient) {}

  getAdminInfo(): Observable<AdminResponse> {
    return this.http.get<AdminResponse>(`${this.baseUrl}/admin-info`);
  }

  updateProfile(request: AdminRequest): Observable<any> {
    return this.http.patch(`${this.baseUrl}/account-info`, request);
  }

  changePassword(request: ChangeAdminPasswordRequest): Observable<any> {
    return this.http.patch(`${this.baseUrl}/changePassword`, request);
  }
}


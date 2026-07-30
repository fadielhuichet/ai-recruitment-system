import {Component, OnInit} from '@angular/core';
import {Header} from '../../Shared/header/header';
import {RouterLink} from '@angular/router';
import {CommonModule, NgIf} from '@angular/common';
import {JobService} from '../../services/services/job-service/job-service';
import {JobResponse} from '../../models/Dto/Job/JobResponse';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-home',
  imports: [
    Header,
    RouterLink,
    CommonModule
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit{
  isAuth=false;
  last3Jobs$?:Observable<JobResponse[]>;

  constructor(private jobService:JobService) {
  }

  ngOnInit() {
    this.isAuthentify();
    this.last3Jobs$ =this.jobService.getLast3Jobs();
  }

  isAuthentify():boolean{
    const token=localStorage.getItem('token');
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (payload) {
        this.isAuth = true;
        return true
      }
      else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }

}

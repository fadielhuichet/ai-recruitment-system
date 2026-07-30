import {AfterViewInit, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {RecruiterStatsResponse} from '../../../models/Dto/Recruiter/RecruiterStatsResponse';
import {JobStatsResponse} from '../../../models/Dto/Job/JobStatsResponse';
import {AdminApplicationStatsResponse} from '../../../models/Dto/Application/AdminApplicationStatsResponse';
import {RecruiterService} from '../../../services/services/recruiter-services/recruiter-service';
import {JobService} from '../../../services/services/job-service/job-service';
import {ApplicationService} from '../../../services/services/Application-service/application-service';
import {RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';

import {Chart, registerables} from 'chart.js';
import {CandidateStatsResponse} from '../../../models/Dto/Candidate/CandidateStatsResponse';
import {CandidateService} from '../../../services/services/Candidate-service/candidate-service';



@Component({
  selector: 'app-admin-dashboard',
  imports: [
    RouterLink,
    CommonModule
  ],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard implements OnInit,AfterViewInit{

  jobStats$!: Observable<JobStatsResponse>;
  applicationStats!: AdminApplicationStatsResponse;
  recruiterStats!:RecruiterStatsResponse;
  candidateStats!:CandidateStatsResponse;



  chart: any;
  private chartReady = false;
  private dataReady = false;
  chart2: any;
  private chartReady2 = false;
  private dataReady2 = false;

  chart3:any;
  private chartReady3=false;
  private dataReady3=false;



  constructor(private recruiterService:RecruiterService,
              private jobService:JobService,
              private applicationService:ApplicationService,
              private candidateService:CandidateService,
              private cdr:ChangeDetectorRef) {
    Chart.register(...registerables)
  }
  ngOnInit() {
    this.candidateService.getStats().subscribe({
      next:(data)=>{
        this.candidateStats=data
        this.dataReady3=true;
        this.tryRenderChart3()
      }
    })
    this.recruiterService.getStats().subscribe({
      next:(data)=>{
        this.recruiterStats=data
        this.dataReady2=true;
        this.tryRenderChart2()
      }
    })
    this.jobStats$ = this.jobService.getStats();
    this.applicationService.getAdminApplicationStats().subscribe({
      next:(data)=>{
        this.applicationStats=data;
        this.dataReady=true;
        this.tryRenderChart();
      }
    })


  }

  ngAfterViewInit() {
    this.chartReady=true;
    this.tryRenderChart();

    this.chartReady2=true;
    this.tryRenderChart2();

    this.chartReady3=true;
    this.tryRenderChart3();
  }
  tryRenderChart3(){
    if (!this.chartReady3 || !this.dataReady3) return;

    if (this.chart3) {
      this.chart.destroy();
    }

    const monthlyC = this.candidateStats.monthlyData;
    const labels = Object.keys(monthlyC);
    const values2 = Object.values(monthlyC);

    this.chart2 = new Chart('monthlyChartCandidate', {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Candidate per Month',
          data: values2,
          backgroundColor: 'rgba(15, 118, 110, 0.25)',
          borderColor: '#0f766e',
          borderWidth: 2,
          borderRadius: 6,
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: ctx => ` ${ctx.parsed.y} recruiters`
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: { stepSize: 1 },
            grid: { color: 'rgba(0,0,0,0.05)' }
          },
          x: {
            grid: { display: false }
          }
        }
      }
    });

    this.cdr.detectChanges();

  }

  tryRenderChart(){
    if (!this.chartReady || !this.dataReady) return;

    if (this.chart) {
      this.chart.destroy();
    }

    const monthly = this.applicationStats.monthlyData;
    const labels = Object.keys(monthly);
    const values = Object.values(monthly);

    this.chart = new Chart('monthlyChart', {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Applications per Month',
          data: values,
          backgroundColor: 'rgba(15, 118, 110, 0.25)',
          borderColor: '#0f766e',
          borderWidth: 2,
          borderRadius: 6,
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: ctx => ` ${ctx.parsed.y} applications`
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: { stepSize: 1 },
            grid: { color: 'rgba(0,0,0,0.05)' }
          },
          x: {
            grid: { display: false }
          }
        }
      }
    });

    this.cdr.detectChanges();
  }
  tryRenderChart2(){
    if (!this.chartReady2 || !this.dataReady2) return;

    if (this.chart2) {
      this.chart.destroy();
    }

    const monthlyR = this.recruiterStats.monthlyData;
    const labels = Object.keys(monthlyR);
    const values2 = Object.values(monthlyR);

    this.chart2 = new Chart('monthlyChartRecruiter', {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Recruiter per Month',
          data: values2,
          backgroundColor: 'rgba(15, 118, 110, 0.25)',
          borderColor: '#0f766e',
          borderWidth: 2,
          borderRadius: 6,
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: ctx => ` ${ctx.parsed.y} recruiters`
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: { stepSize: 1 },
            grid: { color: 'rgba(0,0,0,0.05)' }
          },
          x: {
            grid: { display: false }
          }
        }
      }
    });

    this.cdr.detectChanges();
  }

  protected refresh() {
    this.candidateService.getStats().subscribe({
      next:(data)=>{
        this.candidateStats=data;
      }
    });
    this.recruiterService.getStats().subscribe({
      next:(data)=>{
        this.recruiterStats=data;
      }
    });
    this.jobStats$ = this.jobService.getStats();
    this.applicationService.getAdminApplicationStats().subscribe({
      next:(data)=>{
        this.applicationStats=data
      }

    })
  }
}





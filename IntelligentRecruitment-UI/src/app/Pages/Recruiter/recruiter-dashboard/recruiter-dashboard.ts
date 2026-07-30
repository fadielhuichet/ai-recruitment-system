import { AfterViewInit, Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Chart, registerables } from 'chart.js';

import { RecruiterApplicationStatsResponse } from '../../../models/Dto/Application/RecruiterApplicationStatsResponse';
import { RecruiterJobStatsResponse } from '../../../models/Dto/Job/RecruiterJobStatsResponse';
import { JobService } from '../../../services/services/job-service/job-service';
import { ApplicationService } from '../../../services/services/Application-service/application-service';

@Component({
  selector: 'app-recruiter-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './recruiter-dashboard.html',
  styleUrl: './recruiter-dashboard.css',
})
export class RecruiterDashboard implements OnInit, AfterViewInit {

  jobStats!: RecruiterJobStatsResponse;
  applicationStats!: RecruiterApplicationStatsResponse;

  chart: any;
  private chartReady = false;
  private dataReady = false;

  constructor(
    private jobService: JobService,
    private applicationService: ApplicationService,
    private cdr: ChangeDetectorRef
  ) {
    Chart.register(...registerables);
  }

  ngOnInit(): void {
    this.jobService.getMyJobStats().subscribe(data => {
      this.jobStats = data;
      this.cdr.detectChanges();
    });

    this.applicationService.getRecruiterApplicationStats().subscribe(data => {
      this.applicationStats = data;
      this.dataReady = true;
      this.tryRenderChart();
    });
  }

  ngAfterViewInit(): void {
    this.chartReady = true;
    this.tryRenderChart();
  }

  private tryRenderChart(): void {
    if (!this.chartReady || !this.dataReady) return;

    if (this.chart) {
      this.chart.destroy();
    }

    const weekly = this.applicationStats.weeklyData;
    const labels = Object.keys(weekly);
    const values = Object.values(weekly);

    this.chart = new Chart('weeklyChart', {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Applications this week',
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

  getWeekPercent(value: number, other: number): number {
    const max = Math.max(value, other);
    return max === 0 ? 0 : Math.round((value / max) * 100);
  }
}

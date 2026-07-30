import { Component } from '@angular/core';
import {Header} from '../../header/header';
import {CommonModule} from '@angular/common';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-recruiter-layout',
  imports: [
    Header,
    CommonModule,
    RouterLinkActive,
    RouterLink,
    RouterOutlet
  ],
  templateUrl: './recruiter-layout.html',
  styleUrl: './recruiter-layout.css',
})
export class RecruiterLayout {
  isCollapsed = false;
  isRecruiterRoute= true;
}

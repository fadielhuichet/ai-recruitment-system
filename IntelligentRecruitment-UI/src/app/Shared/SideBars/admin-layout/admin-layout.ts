import { Component } from '@angular/core';
import {Header} from '../../header/header';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {CommonModule, NgClass} from '@angular/common';

@Component({
  selector: 'app-admin-layout',
  imports: [
    CommonModule,
    Header,
    RouterOutlet,
    NgClass,
    RouterLinkActive,
    RouterLink
  ],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.css',
})
export class AdminLayout {
  isCollapsed = false;
}

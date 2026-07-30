import {Component} from '@angular/core';
import {Header} from '../../header/header';
import {RouterModule, RouterOutlet} from '@angular/router';
import {CommonModule} from '@angular/common';


@Component({
  selector: 'app-candidate-layout',
  standalone:true,
  imports: [
    RouterModule,
    Header,
    RouterOutlet,
    CommonModule

  ],
  templateUrl: './candidate-layout.html',
  styleUrl: './candidate-layout.css',
})
export class CandidateLayout {
  isCollapsed=false;
}

import {ChangeDetectorRef, Component, HostListener, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {CommonModule, NgClass} from '@angular/common';
import {TokenService} from '../../services/services/token/token-service';
import {AuthenticationService} from '../../services/services/Authentication-service/authentication-service';
import {AuthResponse} from '../../models/Dto/Auth/AuthResponse';
import {AdminResponse} from '../../models/Dto/Admin/AdminResponse';
import {RecruiterService} from '../../services/services/recruiter-services/recruiter-service';
import {CandidateResponse} from '../../models/Dto/Candidate/CandidateResponse';
import {CandidateService} from '../../services/services/Candidate-service/candidate-service';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
    NgClass,
    CommonModule,
    RouterLinkActive

  ],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements OnInit{
  isMenuOpen = false;
  isProfileMenuOpen: boolean = false;
  isRecruiters=false;
  isAdmins=false;
  isCandidates=false;
  recruiter?:AuthResponse;
  admin?:AdminResponse;
  candidate?:CandidateResponse;

  isVisible = true;
  lastScrollTop = 0;

  // Add this HostListener
  @HostListener('window:scroll', [])
  onWindowScroll() {
    const currentScroll = window.pageYOffset || document.documentElement.scrollTop;

    // If scrolling down AND past the header height (e.g., 80px), hide it
    if (currentScroll > this.lastScrollTop && currentScroll > 80) {
      this.isVisible = false;
    } else {
      // If scrolling up, show it
      this.isVisible = true;
    }

    // Update the last scroll position (prevent negative scrolling issues on mobile)
    this.lastScrollTop = currentScroll <= 0 ? 0 : currentScroll;
  }
  constructor(private router:Router,protected tokenService:TokenService,protected cdr:ChangeDetectorRef,private authService:AuthenticationService,protected recruiterService:RecruiterService) {
  }
  ngOnInit() {
    this.isRecruiter();
    this.isAdmin();
    this.isCandidate();
  }

  protected toLogin() {
    this.router.navigate(['login'])
  }

  protected toJobs() {
    this.router.navigate(['jobs'])
  }

  protected reload() {
    this.router.navigate(['home']);
  }

  loadAdminInfo(){
    this.authService.getAdminInfo().subscribe({
      next:(data)=>{
        this.admin=data
        this.cdr.detectChanges();

      }
    })
  }

  loadRecruiterInfo(){
    this.authService.getRecruiterInfo().subscribe({
      next:(data)=>{
        this.recruiter=data;
        this.cdr.detectChanges();
      }
    })
  }
  toProfile() {
    this.isProfileMenuOpen = false;
     this.router.navigate(['/recruiter-profile']);
  }

  logout() {
    this.isProfileMenuOpen = false;
    this.tokenService.logout();
  }

  isAdmin():boolean{
    const token=localStorage.getItem('token');
    if(!token) return false;
    try {
      const payload=JSON.parse(atob(token.split('.')[1]));
      if (payload && payload.role ==='ADMIN'){
        this.isAdmins=true
        this.loadAdminInfo();
        return true;
      }
      else {
        return false;
      }

    }catch (e){
      return false;
    }
  }
  isRecruiter():boolean{
    const token=localStorage.getItem('token');
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (payload && payload.role === 'RECRUITER') {
        this.isRecruiters = true;
        this.loadRecruiterInfo();
        return true
      }
      else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }
  isCandidate():boolean{
    const token=localStorage.getItem('token');
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (payload && payload.role === 'CANDIDATE') {
        this.isCandidates = true;
        this.loadCandidateInfo();
        return true
      }
      else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }


  protected toDashboard() {
    this.router.navigate(['/recruiter/dashboard']);
  }

  protected toSetting() {
    this.router.navigate(['/settings'])
  }

  protected toProfileAdmin() {
    this.router.navigate(['/admin-profile'])

  }

  loadCandidateInfo(){
    this.authService.getCandidateInfo().subscribe({
      next:(data)=>{
        this.candidate=data;
        this.cdr.detectChanges();
      }
    })
  }


  protected toSettingCandidate() {

  }

  protected toProfileCandidate() {
    this.router.navigate(['/candidate-profile'])

  }
}

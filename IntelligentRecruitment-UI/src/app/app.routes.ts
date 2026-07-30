import { Routes } from '@angular/router';
import { Login } from './Pages/login/login';
import { Register } from './Pages/register/register';
import { Candidates } from './Pages/Admin/candidates/candidates';

import { Home } from './Pages/home/home';
import { Jobs } from './Pages/jobs/jobs';
import { Profile } from './Pages/Recruiter/Recruiter-profile/profile';
import { PageNotFoundComponent } from './page-not-found-component/page-not-found-component';
import { VerifEmail } from './Pages/verif-email/verif-email';
import { VerifCode } from './Pages/verif-code/verif-code';
import { ChangePassword } from './Pages/change-password/change-password';
import {JobDetails} from './Pages/job-details/job-details';
import {RecruiterLayout} from './Shared/SideBars/recruiter-layout/recruiter-layout';
import {MyJobs} from './Pages/Recruiter/Recruiter-Jobs/my-jobs/my-jobs';
import {RecruiterJobDetails} from './Pages/Recruiter/Recruiter-Jobs/recruiter-job-details/recruiter-job-details';
import {RecruiterDashboard} from './Pages/Recruiter/recruiter-dashboard/recruiter-dashboard';
import {PostJob} from './Pages/Recruiter/Recruiter-Jobs/post-job/post-job';
import {recruiterGuard} from './services/guards/Recruiter/recruiter-guard';
import {Applications} from './Pages/Recruiter/Recruiter-applications/applications';
import Settings from './Pages/settings/settings';
import {AdminLayout} from './Shared/SideBars/admin-layout/admin-layout';
import {AdminDashboard} from './Pages/Admin/admin-dashboard/admin-dashboard';
import {Recruiters} from './Pages/Admin/recruiters/recruiters';
import {AdminJobs} from './Pages/Admin/jobs/Adminjobs';
import {adminGuard} from './services/guards/Admin/admin-guard';
import {AdminProfile} from './Pages/Admin/admin-profile/admin-profile';
import {RegisterCandidate} from './Pages/Candidate/register-candidate/register-candidate';
import {CandidateProfile} from './Pages/Candidate/candidate-profile/candidate-profile';
import {CandidateLayout} from './Shared/SideBars/candidate-layout/candidate-layout';
import {candidateGuard} from './services/guards/Candidate/candidate-guard';
import {CandidateApplications} from './Pages/Candidate/candidate-applications/candidate-applications';
import {CandidateDashboard} from './Pages/Candidate/candidate-dashboard/candidate-dashboard';
import {AdminApplications} from './Pages/Admin/applications/AdminApplications';
import {Contact} from './Pages/contact/contact';



export const routes: Routes = [
  {path:'home', component:Home},
  {path:'contact',component:Contact},
  {path:'login',component: Login},
  {path:'verif-email',component:VerifEmail},
  {path:'verif-code',component:VerifCode},
  {path:'change-password',component:ChangePassword},
  {path:'register', component:Register},
  {path:'registerCandidate',component:RegisterCandidate},
  {path:'candidates',component: Candidates},
  {path:'recruiters' ,component: Recruiters},
  {path:'jobs', component:Jobs},
  {path:'job-details/:id',component:JobDetails},
  {
    path:'recruiter',
    component:RecruiterLayout,
    canActivate:[recruiterGuard],
    children:[
      {path: '', redirectTo: 'dashboard', pathMatch: 'full'},
      {path:'dashboard',component:RecruiterDashboard},
      {path:'jobs',component:MyJobs},
      {path:'job-details/:id',component:RecruiterJobDetails},
      {path:'post-job',component:PostJob},
      {path:'applications',component:Applications},

    ]},
  {path:'recruiter-profile', component:Profile},
  {path:'settings',component:Settings},
  {
    path:'admin',
    component:AdminLayout,
    canActivate:[adminGuard],
    children:[
      {path: '', redirectTo: 'admin-dashboard', pathMatch: 'full'},
      {path: 'admin-dashboard',component:AdminDashboard },
      {path: 'recruiters',component: Recruiters},
      {path: 'admin-jobs',component: AdminJobs},
      {path: 'candidates',component: Candidates},
      {path: 'applications',component: AdminApplications}
  ]},
  {path:'admin-profile',component:AdminProfile},
  {path:'candidate',component:CandidateLayout,canActivate:[candidateGuard],children:[
      {path: '', redirectTo: 'candidate-dashboard', pathMatch: 'full'},
      {path: 'candidate-dashboard',component: CandidateDashboard},
      {path: 'my-application',component: CandidateApplications}
    ]},
  {path:'candidate-profile',component:CandidateProfile},
  {path:'', redirectTo:'/home',pathMatch:'full'},
  {path:'**', component:PageNotFoundComponent}






];

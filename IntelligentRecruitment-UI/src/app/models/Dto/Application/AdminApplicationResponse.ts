import {ApplicationStatus} from '../../Enum/ApplicationStatus';

export interface AdminApplicationResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  llmScore: number | null;
  llmAnalysis: string | null;
  cvFilePath: string | null;
  createdAt: string;
  status: ApplicationStatus;
  jobId: number;
  //Job-details
  title: string | null;
  description?: string | null;
  location: string | null;
  jobCreatedAt: string | null;
  //Recruiter-details
  recruiterFirstName: string | null;
  recruiterLastName: string | null;
  recruiterEmail: string | null;
}

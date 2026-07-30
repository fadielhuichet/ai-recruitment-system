import {ApplicationStatus} from '../../Enum/ApplicationStatus';

export interface CandidateApplicationResponse {
  applicationId: number;

  status:ApplicationStatus

  appliedAt: string;

  jobId: number;
  jobTitle: string;
  company: string;

  location: string;
}

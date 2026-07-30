import {ApplicationStatus} from '../../Enum/ApplicationStatus';

export interface ApplicationResponse {
  id:number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  llmScore: number; // BigDecimal côté Java -> number côté TS
  llmAnalysis: string;
  cvFilePath: string;
  createdAt: string; // LocalDateTime -> string (ISO date)
  status: ApplicationStatus;
}

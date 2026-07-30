import { ApplicationStatus } from "../Enum/ApplicationStatus";
import { Job } from "./Job";

export interface Application {
    id: number;
    
      jobId: number;
      job?: Job; // @Transient
    
      candidateFirstName: string;
      candidateLastName: string;
      candidateEmail: string;
      candidatePhone?: string;
    
      cvFilePath: string;
      cvText: string;
    
      llmScore?: number; 
      llmAnalysis?: string;
    
      status: ApplicationStatus;
    
      createdAt: string; 
}
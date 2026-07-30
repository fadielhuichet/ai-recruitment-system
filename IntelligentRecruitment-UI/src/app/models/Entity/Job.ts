import { Application } from "./application";
import { JobCategory } from "../Enum/JobCategory";
import { JobStatus } from "../Enum/JobStatus";
import { Recruiter } from "./recruiter";

export interface Job {
      id: number;
    
      recruiterId: number;
    
      title: string;
      description: string;
      company: string;
      location: string;
    
      applicationLink: string;
    
      status: JobStatus;
    
      createdAt: string;
    
      category?: JobCategory;
      customCategory?: string;
    
      
      recruiter?: Recruiter;
      applications?: Application[];

}
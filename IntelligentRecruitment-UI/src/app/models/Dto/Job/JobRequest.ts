import {JobCategory} from './jobCategory';

export interface JobRequest {
  title: string;
  description: string;
  company: string;
  location: string;
  category: JobCategory; // enum
  customCategory?: string; // optional
}

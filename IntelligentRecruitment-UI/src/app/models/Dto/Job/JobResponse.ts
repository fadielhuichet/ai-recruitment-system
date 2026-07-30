import {JobCategory} from './jobCategory';
import {JobStatus} from '../../Enum/JobStatus';

export interface JobResponse{
  id:number,
  title:string,
  description:string,
  location:string,
  createdAt:string,
  company:string,
  category:JobCategory,
  customCategory:string,
  status:JobStatus,
  applicantCount:number;
  recruiterId:number,
  recruiterEmail:string,
  recruiterCompany:string,
  recruiterFirstName:string,
  recruiterLastName:string,
  recruiterPhone:string,


}

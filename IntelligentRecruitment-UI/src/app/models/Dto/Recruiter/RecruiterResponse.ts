import {Status} from './Status';

export interface RecruiterResponse{
  id:number;
  email:string;
  companyName:string;
  firstName:string;
  lastName:string;
  phone:string;
  createdAt:string;
  status:Status;
  profileImage: string | null;
}

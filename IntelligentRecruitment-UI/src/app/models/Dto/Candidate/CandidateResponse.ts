import {Status} from '../Recruiter/Status';


export interface CandidateResponse{
  id:number
  firstName:string
  lastName:string
  dateOfBirth:string
  email:string
  phone:string
  status:Status
  profileImage:string
  createdAt:string
}

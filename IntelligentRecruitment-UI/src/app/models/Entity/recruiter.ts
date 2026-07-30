import { Job } from "./Job";
import { Role } from "../Enum/Role";
import { Status } from "../Enum/Status";

export interface Recruiter {
     idRecruiter:number;
     email:String;
     password:String;
     phone:String;
     firstName:String;
     lastName:String;
     companyName:String;
     role:Role;
     createdAt:String;
     status:Status;
     jobs?:Job[];
     suspendedAt?: string;
     suspendedBy?: string;
     suspensionReason?: string;

}
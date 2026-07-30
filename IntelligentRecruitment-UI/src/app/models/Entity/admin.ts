import { Role } from "../Enum/Role";

export interface Admin {
     idAdmin:number;
     email:String;
     password:String;
     phone:String;
     firstName:String;
     lastName:String;
     role:Role;
     createdAt:String;
}
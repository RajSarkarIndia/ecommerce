
export interface UserInfo {
  userId:number;
  name:string;
  email:string;
  role:string;
  addresses: Address[];


}
export interface Address {
  id: number;
  address:string;
  pincode:number;
  phoneNumber:string;

}


export interface UserAccount {
  id: string;
  username: string;
  email: string;
  fullName: string;
  role: 'ADMIN' | 'STAFF' | 'CUSTOMER';
  status: 'ACTIVE' | 'DISABLED';
  createdAt: string;
}
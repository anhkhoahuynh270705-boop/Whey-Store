// Staff — Customer & related models

export interface ShippingAddress {
  fullName: string;
  phone: string;
  addressLine: string;
  ward?: string;
  district?: string;
  city: string;
  note?: string;
}

// Order (subset dùng để build customer list)
export interface Order {
  id: number;
  orderNumber: string;
  userId: string;
  status: string;
  totalAmount: number;
  shippingAddress: string;
  paymentMethod: string;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

// Derived model — built from orders on the client side
export interface CustomerSummary {
  userId: string;
  fullName: string;
  phone: string;
  address: string;
  orderCount: number;
  totalSpent: number;
  lastOrderAt: string;
}

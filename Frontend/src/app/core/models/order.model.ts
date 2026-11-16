export interface ShippingAddress {
  fullName: string;
  phone: string;
  addressLine: string;
  ward?: string;
  district?: string;
  city: string;
  note?: string;
}

export interface CheckoutItem {
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
}

export interface CheckoutRequest {
  cartId: string;
  shippingAddress: ShippingAddress;
  paymentMethod: string;
  items: CheckoutItem[];
}

export interface CheckoutResponse {
  orderId: number | null;
  orderNumber: string | null;
  processInstanceId: string;
  message: string;
}

export interface OrderItem {
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface OrderStatusHistory {
  status: string;
  note: string;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface Order {
  id: number;
  orderNumber: string;
  userId: string;
  status: string;
  totalAmount: number;
  shippingAddress: string;
  paymentMethod: string;
  processInstanceId: string | null;
  cancelReason: string | null;
  items: OrderItem[];
  statusHistory: OrderStatusHistory[];
  createdAt: string;
  updatedAt: string;
  completedAt: string | null;
  cancelledAt: string | null;
}

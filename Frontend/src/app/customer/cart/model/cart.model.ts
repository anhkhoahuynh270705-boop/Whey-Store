export interface CartItem {
  productId: number;
  name: string;
  price: number;
  salePrice: number | null;
  quantity: number;
  imageUrl: string | null;
}

export interface CartDto {
  cartOwner: string;
  items: CartItem[];
  totalAmount: number;
  totalQuantity: number;
}

export interface CartItemRequest {
  productId: number;
  quantity: number;
}

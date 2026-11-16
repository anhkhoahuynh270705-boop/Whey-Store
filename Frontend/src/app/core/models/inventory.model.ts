export interface InventoryItem {
  id: number;
  productId: number;
  sku: string;
  availableQty: number;
  reservedQty: number;
  version: number;
}

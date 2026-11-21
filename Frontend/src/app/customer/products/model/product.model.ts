export interface ProductImage {
  id: number | null;
  imageUrl: string;
  isPrimary: boolean;
}

export interface Category {
  id: number;
  slug: string;
  name: string;
  description: string;
  createdAt: string;
}

export interface Brand {
  id: number;
  slug: string;
  name: string;
  description: string;
  logoUrl: string;
  createdAt: string;
}

export interface Product {
  id: number;
  name: string;
  slug: string;
  description: string;
  categoryId: number;
  categoryName: string;
  brandId: number;
  brandName: string;
  price: number;
  salePrice: number | null;
  weightGrams: number | null;
  flavor: string | null;
  servingSize: number | null;
  status: string;
  images: ProductImage[];
  createdAt: string;
  updatedAt: string;
}

export interface ProductListItem {
  id: number;
  name: string;
  slug: string;
  price: number;
  salePrice: number | null;
  brandName: string;
  categoryName: string;
  primaryImage: string | null;
  status: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface ProductFilter {
  search?: string;
  categoryId?: number;
  brandId?: number;
  flavor?: string;
  minPrice?: number;
  maxPrice?: number;
  page?: number;
  size?: number;
  sort?: string;
}

// ── UI-only types dùng trong product-detail ──────────────
export interface GalleryItem {
  id: number | null;
  imageUrl: string;
  label: string;
}

export interface WeightOption {
  label: string;
  grams: number;
  servingSize: number;
  priceMultiplier: number;
}

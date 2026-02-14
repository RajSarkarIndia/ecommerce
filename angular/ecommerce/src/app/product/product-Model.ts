import { ProductStatus } from './Enum/ProductStatus';

export interface ProductImage {

  objectName?: string;
  url:string;
}

export interface ProductDTO {
  productId: number;
  userId: number;
  sku: string;
  title: string;
  description: string;
  price: number;
  stock: number;
  status: ProductStatus;
  categories: string[];
  productImages: ProductImage[];
}

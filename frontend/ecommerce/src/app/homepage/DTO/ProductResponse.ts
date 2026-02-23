import {ProductStatus} from '../../product/Enum/ProductStatus';
import {ProductCategory} from '../../product/Enum/ProductCategory';

export interface ProductImage {
  imageId: number;
  objectName: string;
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
  categories: ProductCategory[];
  images: ProductImage[];
}

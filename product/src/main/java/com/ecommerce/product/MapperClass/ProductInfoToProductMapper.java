package com.ecommerce.product.MapperClass;

import com.ecommerce.product.DTO.ProductInfo;
import com.ecommerce.product.entity.Product;

public class ProductInfoToProductMapper {

    public static Product mapper(ProductInfo productInfo) {

        Product product = new Product();
        product.setSku(productInfo.getSku());
        product.setTitle(productInfo.getTitle());
        product.setVectorEmbedding("");
        product.setDescription(productInfo.getDescription());
        product.setPrice(productInfo.getPrice());
        product.setStock(productInfo.getStock());
        product.setStatus(productInfo.getStatus());
        product.setCategories(productInfo.getCategories());
        return product;
    }
}

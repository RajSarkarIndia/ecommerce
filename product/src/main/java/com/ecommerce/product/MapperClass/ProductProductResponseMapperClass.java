package com.ecommerce.product.MapperClass;

import com.ecommerce.product.DTO.ProductResponse;
import com.ecommerce.product.entity.Product;

public class ProductProductResponseMapperClass {
    public static ProductResponse Mapper(Product product){
        ProductResponse productResponse=new ProductResponse();
        productResponse.setProductId(product.getProductId());
        productResponse.setTitle(productResponse.getTitle());
        productResponse.setPrice(product.getPrice());
        productResponse.setStatus(product.getStatus());
        productResponse.setStock(product.getStock());
        return productResponse;

    }

}

package com.ecommerce.product.MapperClass;

import com.ecommerce.product.DTO.ProductImageDetails;
import com.ecommerce.product.DTO.ProductResponse;
import com.ecommerce.product.GCP.storage.GenerateV4GetObjectSignedUrl;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductImages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductProductResponseMapperClass {

    @Autowired
    private GenerateV4GetObjectSignedUrl signedUrlService;

    public ProductResponse mapIt(Product product) {

        ProductResponse productResponse = new ProductResponse();

        productResponse.setProductId(product.getProductId());
        productResponse.setSku(product.getSku());
        productResponse.setTitle(product.getTitle());
        productResponse.setDescription(product.getDescription());
        productResponse.setPrice(product.getPrice());
        productResponse.setStock(product.getStock());
        productResponse.setStatus(product.getStatus());
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setCategories(product.getCategories());

        // ✅ Generate Signed URLs
        if (product.getProductImages() != null) {

            List<ProductImageDetails> imageDetailsList =
                    product.getProductImages()
                            .stream()
                            .map(image -> {
                                ProductImageDetails details = new ProductImageDetails();
                                details.setObjectName(image.getObjectName());

                                String signedUrl =
                                        signedUrlService.generateV4GetObjectSignedUrl(
                                                image.getObjectName()
                                        );

                                details.setUrl(signedUrl);
                                return details;
                            })
                            .collect(Collectors.toList());

            productResponse.setImages(imageDetailsList);
        }

        return productResponse;
    }
}

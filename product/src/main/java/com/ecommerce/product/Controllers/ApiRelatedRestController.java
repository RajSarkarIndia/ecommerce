package com.ecommerce.product.Controllers;


import com.ecommerce.product.DAO.ProductRepository;
import com.ecommerce.product.DTO.ProductResponse;
import com.ecommerce.product.MapperClass.ProductProductResponseMapperClass;
import com.ecommerce.product.entity.Product;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/productApi")
public class ApiRelatedRestController {

    private final ProductRepository productRepository;

    ApiRelatedRestController(ProductRepository productRepository){
        this.productRepository=productRepository;

    }

@PostMapping("/fetchProductById/{productId}/{quantity}")
    public ProductResponse fetchProductById(@PathVariable("productId")Integer productId,@PathVariable("quantity") Integer quantity){

Product product=productRepository.findByProductId(productId);

    ProductResponse productResponse=ProductProductResponseMapperClass.Mapper(product);

    if(quantity<=product.getStock())
                    product.setStock(product.getStock()-quantity);

    productRepository.save(product);

    return productResponse;

}






}

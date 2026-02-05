package com.ecommerce.product.DAO;


import com.ecommerce.product.entity.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImages,Integer> {
}

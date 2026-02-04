package com.ecommerce.product.DAO;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Integer, Product> {
    boolean existsByUserIdAndTitle(Integer userId, String title);//if same exist then update the product. No new product Allowed
}

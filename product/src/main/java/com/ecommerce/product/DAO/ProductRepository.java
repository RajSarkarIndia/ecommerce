package com.ecommerce.product.DAO;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByUserIdAndTitle(Integer userId, String title);//if same exist then update the product. No new product Allowed
    Product findByUserIdAndProductId(Integer userId,Integer ProductId);
    Product findByProductId(Integer ProductId);
    List<Product> findAllByUserId(Integer userId);
    List<Product> findAllByCategories(String category);
}

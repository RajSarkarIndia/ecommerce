package com.ecommerce.Authentication.DAO;

import com.ecommerce.Authentication.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);
    boolean existsByUsername(String username);


    boolean existsByEmail(String email);
}

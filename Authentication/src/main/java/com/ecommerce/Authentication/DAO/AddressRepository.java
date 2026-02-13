package com.ecommerce.Authentication.DAO;


import com.ecommerce.Authentication.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.*;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    Optional<Address> findById(Integer id);
    void deleteById(Integer id);
}

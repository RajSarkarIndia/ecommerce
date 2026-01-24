package com.ecommerce.Authentication.Entity;

import com.ecommerce.Authentication.DAO.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UDetailsService implements UserDetailsService {
    private UserRepository userRepository;
    Logger logger = Logger.getLogger("UDetailsService.class");

    public UDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserDetails user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            logger.warning("email Not found");
            throw new UsernameNotFoundException("Email Not found");

        }

        return user;
    }
}

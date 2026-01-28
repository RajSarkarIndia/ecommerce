package com.ecommerce.Authentication.JwtRestController;


import com.ecommerce.Authentication.DAO.UserRepository;
import com.ecommerce.Authentication.DTO.LoginForm;
import com.ecommerce.Authentication.Entity.User;
import com.ecommerce.Authentication.JWT.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class JWTRestController {
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    public JWTRestController(JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginForm loginForm) {

        User user = userRepository.findByEmail(loginForm.getEmail());

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        if (!passwordEncoder.matches(loginForm.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401)
                    .body("Wrong Username or Password");
        }
        String token = jwtUtil.createToken(user.getEmail(), user.getRole());
        return ResponseEntity.status(200)
                .body("token=" + token);//use split in ts to split

    }

    //Register user
    @PostMapping("/user/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {//gets role as a option in form.
        //base check
        //check username if exist
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(409)
                    .body("Username already exists");
        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);


        return ResponseEntity.status(200)
                .body("User registered successfully");

    }


}




















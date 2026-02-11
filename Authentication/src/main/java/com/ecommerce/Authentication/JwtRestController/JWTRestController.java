package com.ecommerce.Authentication.JwtRestController;


import com.ecommerce.Authentication.DAO.UserRepository;
import com.ecommerce.Authentication.DTO.LoginForm;
import com.ecommerce.Authentication.DTO.UpdateUser;
import com.ecommerce.Authentication.Entity.User;
import com.ecommerce.Authentication.JWT.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
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
        String token = jwtUtil.createToken(user.getEmail(), user.getRole(), user.getUserId());
        return ResponseEntity.status(200)
                .body(token);

    }

    //Register user
    @PostMapping("/user/register")
    @Transactional
    public ResponseEntity<String> registerUser(@RequestBody User user) {//gets role as a option in form.
        //base check
        //check username if exist
        if (userRepository.existsByEmail((user.getUsername()))) {
            return ResponseEntity.status(409)
                    .body("Username already exists");
        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);


        return ResponseEntity.status(200)
                .body("User registered successfully");

    }

    //get User Information
    @PostMapping("getUser")
    public ResponseEntity<User> getUserDeatils(@RequestHeader("Authentication") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String jwt = authHeader.substring(7);

        //validate the JWT then fecth claim
        if (!jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(401)
                    .body(null);

        }

        //get User information
        Claims userInfo = jwtUtil.extractAllClaims(jwt);
        String username = userInfo.getSubject();
        String role = userInfo.get("role", String.class);
        Integer userId = userInfo.get("userId", Integer.class);
        User user = userRepository.findByEmail(username);
        if (user != null && username.equals(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(user);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
    }

//update the user

    @PutMapping("/editUser")
    @Transactional
    public ResponseEntity<String> updateUser(@RequestHeader("Authentication") String authHeader, @RequestBody UpdateUser newUser) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String jwt = authHeader.substring(7);

        //validate the JWT then fecth claim
        if (!jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(401)
                    .body(null);

        }

        //get User information
        Claims userInfo = jwtUtil.extractAllClaims(jwt);
        String username = userInfo.getSubject();
        String role = userInfo.get("role", String.class);
        Integer userId = userInfo.get("userId", Integer.class);
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getUsername().equals(username)) {
            user.setEmail(newUser.getEmail());
            user.setName(newUser.getName());
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("User Updated");
}
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("User not updated");


    }
}




















package com.ecommerce.Authentication.JwtRestController;


import com.ecommerce.Authentication.DAO.AddressRepository;
import com.ecommerce.Authentication.DAO.UserRepository;
import com.ecommerce.Authentication.DTO.AddressDTO;
import com.ecommerce.Authentication.DTO.LoginForm;
import com.ecommerce.Authentication.DTO.UpdateUser;
import com.ecommerce.Authentication.Entity.Address;
import com.ecommerce.Authentication.Entity.User;
import com.ecommerce.Authentication.JWT.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
public class JWTRestController {
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private AddressRepository addressRepository;

    public JWTRestController(JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserRepository userRepository, AddressRepository addressRepository) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.addressRepository=addressRepository;
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
    //get User Information
    @GetMapping("/getUser")
    @Transactional(readOnly=true)
    public ResponseEntity<User> getUserDeatils(@RequestHeader(name="Authorization"/*,required=false*/) String authHeader, HttpServletRequest request) {

        if (authHeader == null && request.getAttribute("Authorization")==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
     /*   if(authHeader==null) {
            System.out.println("set from getAttribute");

            authHeader = request.getAttribute("Authorization").toString();
        }*/
        // 🔥 ADD THIS (safe handling)
        if (authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
        }

        if (!jwtUtil.validateToken(authHeader)) {
            return ResponseEntity.status(401).body(null);
        }

        Claims userInfo = jwtUtil.extractAllClaims(authHeader);
        String username = userInfo.getSubject();
        String role = userInfo.get("role", String.class);
        Integer userId = userInfo.get("userId", Integer.class);

        User user = userRepository.findByEmail(username);
        if (user != null && username.equals(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

//update the user

    @PutMapping("/editUser")
    @Transactional
    public ResponseEntity<String> updateUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateUser newUser) {

        if (authHeader == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        if (authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
        }

        if (!jwtUtil.validateToken(authHeader)) {
            return ResponseEntity.status(401).body(null);
        }

        Claims userInfo = jwtUtil.extractAllClaims(authHeader);
        String username = userInfo.getSubject();
        String role = userInfo.get("role", String.class);
        Integer userId = userInfo.get("userId", Integer.class);

        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getUsername().equals(username)) {
            user.setEmail(newUser.getEmail());
            user.setName(newUser.getName());
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK).body("User Updated");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("User not updated");
    }

    @PostMapping("/addAddress")
    @Transactional
    public ResponseEntity<String> addAddress(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AddressDTO dto) {

        if (authHeader == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        if (authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
        }

        if (!jwtUtil.validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }

        Claims claims = jwtUtil.extractAllClaims(authHeader);
        Integer userId = claims.get("userId", Integer.class);

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Address address = new Address();
        address.setAddress(dto.getAddress());
        address.setPincode(dto.getPincode());
        address.setPhoneNumber(dto.getPhoneNumber());
        address.setUser(user);

        addressRepository.save(address);

        return ResponseEntity.ok("Address Added Successfully");
    }

    // 🔹 Update Address
    @PutMapping("/updateAddress/{id}")
    @Transactional
    public ResponseEntity<String> updateAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id,
            @RequestBody AddressDTO dto) {

        if (authHeader == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        if (authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
        }

        if (!jwtUtil.validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }

        Address address = addressRepository.findById(id).orElse(null);
        if (address == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Address not found");
        }

        address.setAddress(dto.getAddress());
        address.setPincode(dto.getPincode());
        address.setPhoneNumber(dto.getPhoneNumber());

        addressRepository.save(address);

        return ResponseEntity.ok("Address Updated Successfully");
    }


    @DeleteMapping("/deleteAddress/{id}")
    @Transactional
    public ResponseEntity<String> deleteAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id) {

        if (authHeader == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        if (authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
        }

        if (!jwtUtil.validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }

        if (!addressRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Address not found");
        }

        addressRepository.deleteById(id);

        return ResponseEntity.ok("Address Deleted Successfully");
    }

}




















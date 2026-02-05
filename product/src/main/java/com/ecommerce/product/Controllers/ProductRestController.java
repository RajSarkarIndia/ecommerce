package com.ecommerce.product.Controllers;
//Update the GCP from static -> Bean

import com.ecommerce.product.DAO.ProductImageRepository;
import com.ecommerce.product.GCP.storage.*;
import com.ecommerce.product.JWT.JwtUtil;
import com.ecommerce.product.DAO.ProductRepository;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductImages;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@RestController
@RequestMapping("/product")
public class ProductRestController {

    private final ProductRepository productRepository;
    private final JwtUtil jwtUtil;
    private final ProductImageRepository productImageRepository;
    private final GcsDeleteService gcsDeleteService;
    private final GenerateV4PutObjectSignedUrl generateV4PutObjectSignedUrl;

    public ProductRestController(ProductRepository productRepository, JwtUtil jwtUtil, ProductImageRepository productImageRepository, GcsDeleteService gcsDeleteService, GenerateV4PutObjectSignedUrl generateV4PutObjectSignedUrl ) {
        this.productRepository = productRepository;
        this.jwtUtil = jwtUtil;
        this.productImageRepository=productImageRepository;
        this.gcsDeleteService=gcsDeleteService;
        this.generateV4PutObjectSignedUrl=generateV4PutObjectSignedUrl;

    }

    //Insert a product
    @PostMapping("new")
    @Transactional
    public ResponseEntity<String> addNewProduct(@ModelAttribute Product product, @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String jwt = authHeader.substring(7);

        //validate the JWT then fecth claim
        if (!jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(401)
                    .body("Invalid Token");

        }

        //get User information
        Claims userInfo = jwtUtil.extractAllClaims(jwt);
        String username = userInfo.getSubject();
        String role = userInfo.get("role", String.class);
        Integer userId = userInfo.get("userId", Integer.class);

        if (product != null && "ROLE_SELLER".equals(role) && userId != null && !productRepository.existsByUserIdAndTitle(userId, product.getTitle())) {
            product.setUserId(userId);
            //vector embedding
            product.setVectorEmbedding("");//Add vector embedding OLLAMA
            productRepository.save(product);
            return ResponseEntity.status(201)
                    .body("Product Added success fully");

        }


        return ResponseEntity.status(403)
                .body("Adding a product is not allowed 403 forbidden");

    }
    //Image upload form


    @PostMapping("/add/image/{productId}")
    public ResponseEntity<String> addImage(@PathVariable Integer productId,@RequestParam MultipartFile image,@RequestHeader("Authorization") String authHeader){

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String jwt = authHeader.substring(7);

        //validate the JWT then fecth claim
        if (!jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(401)
                    .body("Invalid Token");

        }

        //get User information
        Claims userInfo = jwtUtil.extractAllClaims(jwt);
        String username = userInfo.getSubject();
        String role = userInfo.get("role", String.class);
        Integer userId = userInfo.get("userId", Integer.class);
        Product product=productRepository.findByUserIdAndProductId(userId,productId);
        if(product!=null){
            String uuid= UUID.randomUUID().toString();
            ProductImages imageInfo=new ProductImages();
            imageInfo.setObjectName(uuid);
            imageInfo.setProduct(product);
            //add the image
            try{
                generateV4PutObjectSignedUrl.uploadFile(image,uuid);


            }catch(Exception e){
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(e.getMessage());
            }


            product.getProductImages().add(imageInfo);
            imageInfo.setProduct(product);
            productImageRepository.save(imageInfo);
            productRepository.save(product);
            return ResponseEntity.status(201)
                    .body("Upload Successfull");
        }


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
    }
//delete image
    @DeleteMapping("/delete/image/{productId}/{imageId}")
    public ResponseEntity<String> deleteImage(
            @PathVariable Integer productId,
            @PathVariable Integer imageId,
            @RequestHeader("Authorization") String authHeader) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Missing or invalid Authorization header");
            }

            String jwt = authHeader.substring(7);

            if (!jwtUtil.validateToken(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid Token");
            }

            Claims userInfo = jwtUtil.extractAllClaims(jwt);
            Integer userId = userInfo.get("userId", Integer.class);

            Product product = productRepository.findByUserIdAndProductId(userId, productId);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product not found");
            }

            ProductImages image = productImageRepository.findById(imageId).orElse(null);
            if (image == null || !image.getProduct().getProductId().equals(productId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Image not found for this product");
            }

            boolean deleted=gcsDeleteService.deleteObject(image.getObjectName());


            productImageRepository.delete(image);
            product.getProductImages().remove(image);
            productRepository.save(product);

            return ResponseEntity.status(200)
                    .body("Image deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Delete failed: " + e.getMessage());
        }
    }

    //delete the product
    @DeleteMapping("/delete/{productId}")
    @Transactional
    public ResponseEntity<String> deleteProduct(@RequestHeader("Authorization") String authHeader,@PathVariable Integer productId) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Missing or invalid Authorization header");
            }

            String jwt = authHeader.substring(7);

            if (!jwtUtil.validateToken(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid Token");
            }

            Claims userInfo = jwtUtil.extractAllClaims(jwt);
            Integer userId = userInfo.get("userId", Integer.class);

            Product product = productRepository.findByUserIdAndProductId(userId, productId);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product not found");
            }

            for (ProductImages image : product.getProductImages()) {
                gcsDeleteService.deleteObject(image.getObjectName());
                productImageRepository.delete(image);
            }

            productRepository.delete(product);

            return ResponseEntity.status(200)
                    .body("Product and all images deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Delete failed: " + e.getMessage());
        }
    }

    @PutMapping("/update/{productId}")
    @Transactional
    public ResponseEntity<String> updateProduct(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer productId,
            @ModelAttribute Product updatedProduct) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Missing or invalid Authorization header");
            }

            String jwt = authHeader.substring(7);

            if (!jwtUtil.validateToken(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid Token");
            }

            Claims userInfo = jwtUtil.extractAllClaims(jwt);
            Integer userId = userInfo.get("userId", Integer.class);
            String role = userInfo.get("role", String.class);

            Product existingProduct = productRepository.findByUserIdAndProductId(userId, productId);
            if (existingProduct == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product not found or not owned by user");
            }

            if (!"ROLE_SELLER".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only sellers can update products");
            }

            existingProduct.setTitle(updatedProduct.getTitle());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setStock(updatedProduct.getStock());
            existingProduct.setStatus(updatedProduct.getStatus());
            existingProduct.setCategories(updatedProduct.getCategories());

            productRepository.save(existingProduct);

            return ResponseEntity.status(200)
                    .body("Product updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Update failed: " + e.getMessage());
        }
    }




}





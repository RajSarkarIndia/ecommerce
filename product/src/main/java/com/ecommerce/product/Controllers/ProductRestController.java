package com.ecommerce.product.Controllers;
//Update the GCP from static -> Bean

import com.ecommerce.product.DAO.ProductImageRepository;
import com.ecommerce.product.DTO.ProductImageDetails;
import com.ecommerce.product.DTO.ProductInfo;
import com.ecommerce.product.DTO.ProductResponse;
import com.ecommerce.product.Enum.ProductCategory;
import com.ecommerce.product.GCP.storage.*;
import com.ecommerce.product.JWT.JwtUtil;
import com.ecommerce.product.DAO.ProductRepository;
import com.ecommerce.product.MapperClass.ProductInfoToProductMapper;
import com.ecommerce.product.MapperClass.ProductProductResponseMapperClass;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductImages;
import io.jsonwebtoken.Claims;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/product")
public class ProductRestController {

    private final ProductRepository productRepository;
    private final JwtUtil jwtUtil;
    private final ProductImageRepository productImageRepository;
    private final GcsDeleteService gcsDeleteService;
    private final GenerateV4PutObjectSignedUrl generateV4PutObjectSignedUrl;
    private final ProductProductResponseMapperClass productProductResponseMapperClass;

    public ProductRestController(ProductRepository productRepository, JwtUtil jwtUtil, ProductImageRepository productImageRepository, GcsDeleteService gcsDeleteService, GenerateV4PutObjectSignedUrl generateV4PutObjectSignedUrl,ProductProductResponseMapperClass productProductResponseMapperClass1) {
        this.productRepository = productRepository;
        this.jwtUtil = jwtUtil;
        this.productImageRepository = productImageRepository;
        this.gcsDeleteService = gcsDeleteService;
        this.generateV4PutObjectSignedUrl = generateV4PutObjectSignedUrl;
        this.productProductResponseMapperClass = productProductResponseMapperClass1;
    }

    //Insert a product
    @PostMapping("/new")
    @Transactional
    public ResponseEntity<String> addNewProduct(@RequestBody ProductInfo productInfo, @RequestHeader("Authorization") String authHeader) {
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

        if (productInfo != null && "ROLE_SELLER".equals(role) && userId != null && !productRepository.existsByUserIdAndTitle(userId, productInfo.getTitle())) {
            Product product = ProductInfoToProductMapper.mapper(productInfo);
            product.setUserId(userId);
            productRepository.save(product);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Product Added success fully");

        }


        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Adding a product is not allowed forbidden");

    }
    //Image upload form


    @PostMapping("/add/image/{productId}")
    public ResponseEntity<String> addImage(@PathVariable Integer productId, @RequestParam("image") MultipartFile image, @RequestHeader("Authorization") String authHeader) {

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
        Product product = productRepository.findByUserIdAndProductId(userId, productId);
        if (product != null) {
            String uuid = UUID.randomUUID().toString();
            ProductImages imageInfo = new ProductImages();
            imageInfo.setObjectName(uuid);
            imageInfo.setProduct(product);
            //add the image
            try {
                generateV4PutObjectSignedUrl.uploadFile(image, uuid);


            } catch (Exception e) {
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

            boolean deleted = gcsDeleteService.deleteObject(image.getObjectName());
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to delete from GCS");
            }

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

    //delete product
    @DeleteMapping("/delete/{productId}")
    @Transactional
    public ResponseEntity<String> deleteProduct(@RequestHeader("Authorization") String authHeader, @PathVariable Integer productId) {

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
            @RequestBody Product updatedProduct) {

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

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Product updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Update failed: " + e.getMessage());
        }
    }

    //get All product by UserId
    @GetMapping("/postedProducts")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ProductResponse>> getAllProductOfUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null);
            }

            String jwt = authHeader.substring(7);

            if (!jwtUtil.validateToken(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null);
            }

            Claims userInfo = jwtUtil.extractAllClaims(jwt);
            Integer userId = userInfo.get("userId", Integer.class);
            String role = userInfo.get("role", String.class);

            List<Product> allProductOfUser = productRepository.findAllByUserId(userId);
            //convert list of product into list of ProductInfo
            List<ProductResponse> productResponseList = new ArrayList<>();
            for (Product product : allProductOfUser) {
                productResponseList.add(productProductResponseMapperClass.mapIt(product));
            }
            if (allProductOfUser != null && role.equals("ROLE_SELLER")) {

                return ResponseEntity.status(HttpStatus.OK)
                        .body(productResponseList);

            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);


        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    //find all product by Category
    @GetMapping("/productCategory/{category}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ProductResponse>> productCatrgory(
            @PathVariable ProductCategory category){


            List<Product> allProductOfCategory =
                    productRepository.findAllByCategories(category);

            List<ProductResponse> responseList = new ArrayList<>();

            for (Product product : allProductOfCategory) {
                ProductResponse response = productProductResponseMapperClass.mapIt(product);
                responseList.add(response);
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseList);

    }
//view Product
    @GetMapping("/view/{productId}")
    public ResponseEntity<ProductResponse> viewProduct(@PathVariable Integer productId){
        Product product=productRepository.findByProductId(productId);
        if(product!=null) {
            ProductResponse productResponse=productProductResponseMapperClass.mapIt(product);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(productResponse);
        }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(null);
    }


//buy

    @PutMapping("/buy/{productId}/{quantity}")
    @Transactional
    public ResponseEntity<?> fetchProductById(
            @PathVariable Integer productId,
            @PathVariable Integer quantity) {

        Product product = productRepository.findByProductId(productId);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }

        if (quantity <= 0) {
            return ResponseEntity.badRequest()
                    .body("Invalid quantity");
        }

        if (quantity > product.getStock()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Not enough stock available");
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        ProductResponse response =
                productProductResponseMapperClass.mapIt(product);

        return ResponseEntity.ok(response);
    }

    //fetch all product
    @GetMapping("all")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ProductResponse>> allProduct(){
        List<Product> allProduct=productRepository.findAll();
        List<ProductResponse>allProductResponse=new ArrayList<>();
        for(Product product:allProduct){
           ProductResponse productResponse= productProductResponseMapperClass.mapIt(product);
           allProductResponse.add(productResponse);
        }
return ResponseEntity.status(HttpStatus.OK)
        .body(allProductResponse);

    }






}
















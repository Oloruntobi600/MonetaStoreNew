package com.monetastore.monetastore.controllers;

import com.monetastore.monetastore.Models.Product;
import com.monetastore.monetastore.Models.User;
import com.monetastore.monetastore.dto.ProductDto;
import com.monetastore.monetastore.services.ProductRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductRepository repo;


    @GetMapping
    public String showProductList(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("user");

        // Retrieve products associated with the logged-in user
        List<Product> userProducts = repo.findByUserId(user.getId(), Sort.by(Sort.Direction.ASC, "id"));

        if (userProducts.isEmpty()) {
            model.addAttribute("products", null);
        } else {
            model.addAttribute("products", userProducts);
        }

        return "products/index";
    }


    @GetMapping("/create")
    public String showCreatePage (Model model){
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";
    }

    @PostMapping ("/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result, HttpSession session){
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");

        if (productDto.getImageFile().isEmpty()){
            result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
        }

        if (result.hasErrors()){
            return "products/CreateProduct";
        }

        //saving image file
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() +"_" + image.getOriginalFilename();

        try{
            String uploadDir = "public/images/";
            Path uploadpath = Paths.get(uploadDir);

            if (!Files.exists(uploadpath)){
                Files.createDirectories(uploadpath);
            }
            try (InputStream inputStream = image.getInputStream()){
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            }catch (Exception ex){
            System.out.println("Exception: " + ex.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFilesName(storageFileName);
        product.setUser(user);

        repo.save(product);

        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id, HttpSession session){

        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }

        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String updateProduct(Model model, @RequestParam int id, @Valid @ModelAttribute ProductDto productDto,
                                BindingResult result, HttpSession session){
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");

        try{
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            if (result.hasErrors()){
                return "products/EditProduct";
            }

            if (!productDto.getImageFile().isEmpty()){
                //delete old image
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFilesName());

                try{
                  Files.delete((oldImagePath));

                }catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
            }

                //save new image file
                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()){
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImageFilesName(storageFileName);
            }
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            product.setUser(user);
            repo.save(product);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());

        }
        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id, HttpSession session){
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        try {
            Product product = repo.findById(id).get();

            //delete product image
            Path imagePath = Paths.get("public/images/" + product.getImageFilesName());

            try{
                Files.delete(imagePath);
            }catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }

            //delete the product
            repo.delete(product);


        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

            return "redirect:/products";
    }
}

package com.boostmytool.beststore.controller;

import com.boostmytool.beststore.models.product;
import com.boostmytool.beststore.models.productDto;
import com.boostmytool.beststore.services.productRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private productRepository repo;
    @GetMapping({"","/"})
public String showProductList(Model model) {
    List<product> products=repo.findAll();
    model.addAttribute("products",products);
    return "products/index";
}
@GetMapping("/create")
    public String showCreatePage(Model model){
    productDto productDto= new productDto();
    model.addAttribute("productDto",productDto);
    return "products/createproducts";
}
@PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute productDto productDto,
                                BindingResult result){
       if(productDto.getImageFile().isEmpty()){
           result.addError(new FieldError("productDto","imageFile","The image file is required"));
       }
       if(result.hasErrors()){
           return "products/createproducts";
       }

       //save image file
    MultipartFile image=productDto.getImageFile();
    Date createdAt=new Date();
    String storageFileName=createdAt.getTime() + "_" +image.getOriginalFilename();
    try{
        String uploadDir="public/images/";
        Path uploadPath= Paths.get(uploadDir);
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        try(InputStream inputStream =image.getInputStream()) {
            Files.copy(inputStream,Paths.get(uploadDir + storageFileName),
                    StandardCopyOption.REPLACE_EXISTING);
        }}
    catch (Exception ex){
        System.out.println("Exception:" +ex.getMessage());
        }
    product product=new product();
    product.setName(productDto.getName());
    product.setBrand(productDto.getBrand());
    product.setCategory(productDto.getCategory());
    product.setPrice(productDto.getPrice());
    product.setCreatedAt(createdAt);
    product.setDescription(productDto.getDescription());
    product.setImageFileName(storageFileName);
    repo.save(product);

    return "redirect:/products";
    }
    @GetMapping("edit")
    public String showEditPage(Model model, @RequestParam int id){
        try {
            product product=repo.findById(id).get();
            model.addAttribute("product",product);
            productDto productDto=new productDto();
            product.setName(product.getName());
            product.setBrand(product.getBrand());
            product.setCategory(product.getCategory());
            product.setPrice(product.getPrice());
            product.setDescription(product.getDescription());
            model.addAttribute("productDto",productDto);


        }catch (Exception ex){
            System.out.println("Exception: " + ex.getMessage());
        }
        return "products/EditProduct";
    }
    @PostMapping("/edit")
    public  String updateProduct(Model model,@RequestParam int id,@Valid @ModelAttribute productDto productDto,
                                 BindingResult result){
        try {
            product product = repo.findById(id).get();
            model.addAttribute("product", product);
            if (result.hasErrors()) {
                return "products/Editproduct";
            }
            if (!productDto.getImageFile().isEmpty()) {
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }
                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImageFileName(storageFileName);

            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());

            product.setDescription(productDto.getDescription());
            repo.save(product);
        }
                catch (Exception ex){
                    System.out.println("Exception: " + ex.getMessage());
                }
                return "redirect:/products";


    }

    @GetMapping("/delete")
    public String deletProduct(@RequestParam int id){
        try{
product product=repo.findById(id).get();
Path imagepath =Paths.get("public/images/" +product.getImageFileName());
        try{
        Files.delete(imagepath);

        }
        catch (Exception ex){
            System.out.println("Exception: " +ex.getMessage());
        }
        repo.delete(product);
        }
        catch (Exception ex){
            System.out.println("Exception: " +ex.getMessage());
        }
        return "redirect:/products";

}}


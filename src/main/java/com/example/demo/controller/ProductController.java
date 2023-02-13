package com.example.demo.controller;


import com.example.demo.CurrentuserEmail;
import com.example.demo.ImgSaver;
import com.example.demo.accessingdatamysql.Product;
import com.example.demo.accessingdatamysql.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.tool.schema.spi.SqlScriptException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/api1.0/product")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    private static Logger searchLog = LogManager.getLogger("SearchFile");
    private static Logger exceptionLog = LogManager.getLogger("exceptions");


    @GetMapping
    public String boardFromEx(){
        CurrentuserEmail.setEmail(null);
        return "redirect:/api1.0/product/board";
    }

    @GetMapping("/board")
    public String mainPage(Model model){
        Iterable<Product> productIterable = productRepository.findAll();
        List<Product> arrayList = new ArrayList<>();
        productIterable.forEach(arrayList::add);
        model.addAttribute("products",arrayList);
        model.addAttribute("userEmail", CurrentuserEmail.getEmail());

        return "index";
    }

    @PostMapping("/add")
    public String addProduct(@RequestParam("name") String name, @RequestParam("description") String description, @RequestParam("minprice") int minPrice, @RequestParam("file") MultipartFile img, Model model){
        String email = CurrentuserEmail.getEmail();
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setMinPrice(minPrice);
        product.setOwnerEmail(email);
        product.setStatus(true);
        product.setCurrentPrice(minPrice);
        product.setCurrentBuyer(null);


        String pName = new ImgSaver(img).saveImg();
        product.setImgHref(pName);

        productRepository.save(product);
        model.addAttribute("userEmail",email);
        return "profile";
    }

    public void delete(Integer id){
        Optional<Product> product = productRepository.findById(id);
        product.ifPresent(value -> searchLog.info(value.getOwnerEmail() + " made a sale" + "\n" + value.getCurrentBuyer() + " is a buyer !"));
        productRepository.deleteById(id);
    }


    @PutMapping("/{id}")
    public String makeABid(@PathVariable("id") Integer id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent() && CurrentuserEmail.getEmail() != null) {
            Product product = optionalProduct.get();

            boolean flag = !Objects.equals(product.getOwnerEmail(), CurrentuserEmail.getEmail());

            if (product.getCurrentBuyer() == null && flag) {
                searchLog.info("First bet made by " + CurrentuserEmail.getEmail());
                product.setCurrentPrice(product.getCurrentPrice() + 5);
                product.setCurrentBuyer(CurrentuserEmail.getEmail());
                productRepository.save(product);
                searchLog.info("countdown started.. 60 seconds before ending auction sales");
                new Thread(()->{
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        exceptionLog.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    try {
                        delete(product.getId());
                    } catch (SqlScriptException e) {
                        exceptionLog.error(e.getMessage());
                    }
                }).start();

                return "redirect:/api1.0/product/board";
            } else if (product.getCurrentBuyer() != null && flag) {
                product.setCurrentPrice(product.getCurrentPrice() + 5);

                searchLog.info(product.getCurrentBuyer() + "твоя ставка перебита" + "\n" + CurrentuserEmail.getEmail() + " последний покупатель!");

                product.setCurrentBuyer(CurrentuserEmail.getEmail());
                productRepository.save(product);
                return "redirect:/api1.0/product/board";
            } else if (!flag) {
                return "buyYourselfProduct";
            } else if ((!product.getCurrentBuyer().isEmpty()) && product.getCurrentBuyer().equals(product.getCurrentBuyer()) && flag) {
                return "dontChanged";
            }
        }
        return "needAuthorization";
    }
}

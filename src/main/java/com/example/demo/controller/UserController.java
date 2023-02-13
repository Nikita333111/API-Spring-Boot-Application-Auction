package com.example.demo.controller;

import com.example.demo.CurrentuserEmail;
import com.example.demo.accessingdatamysql.User;
import com.example.demo.accessingdatamysql.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/api1.0/users")
public class UserController {
    private UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping(path = "/login")
    public String logPage(){
        return "login";
    }
    @GetMapping("/registration")
    public String regPage(){
        return "registration";
    }


    @PostMapping(path = "/reg")
    public @ResponseBody String registerUser(@RequestParam("email") String email, @RequestParam("password") String password){
        for (User user: userRepository.findAll()) {
            if (user.getEmail().equals(email))
                return "such email already exists";
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        userRepository.save(user);
        return "saved";
    }

    @GetMapping(path = "/log")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password, Model model){
        for (User user: userRepository.findAll()) {
            if(user.getEmail().equals(email) && user.getPassword().equals(password)){
                model.addAttribute("userEmail",email);
                CurrentuserEmail.setEmail(email);
                return "profile";
            }
        }
        return "userDoesntExist";
    }
}

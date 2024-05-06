package com.monetastore.monetastore.controllers;


import com.monetastore.monetastore.Models.User;
import com.monetastore.monetastore.Service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public String login (HttpSession session, User login, Model model) {
        User user = loginService.login(login.getUsername(), login.getPassword());
        if(user != null) {
            session.setAttribute("user", user);
            return "redirect:/products";
        }
        model.addAttribute("errorMessage", "Invalid Username/Password.");
        System.out.println("Login working");
        return  "products/login";
    }
    @GetMapping("/login")
    public String adminLogin(Model model){
        model.addAttribute("user", new User());
        model.addAttribute("invalid", null);
        return "products/login";
        }
    @GetMapping("/logout")
    public String logout(Model model, HttpSession session) {
        if (session != null) {
            session.invalidate();
        }

        model.addAttribute("user", new User());
        model.addAttribute("invalid", null);
        return "redirect:/products/login";
    }

    @RequestMapping(value = {"/logout"}, method = RequestMethod.POST)
    public String logoutDo(HttpServletRequest request, HttpServletResponse response)
    {


        return "redirect:/login";
    }

}
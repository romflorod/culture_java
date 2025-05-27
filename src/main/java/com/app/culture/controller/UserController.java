package com.app.culture.controller;

import com.app.culture.model.User;
import com.app.culture.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("authenticated", true);
        }
        return "index";
    }
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }
    
    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute User user, HttpSession session, Model model) {
        if (userService.authenticateUser(user.getUsername(), user.getPassword())) {
            // Almacenar información del usuario en la sesión
            session.setAttribute("username", user.getUsername());
            
            // Obtener usuario completo para almacenar el ID en sesión (útil para operaciones posteriores)
            Optional<User> fullUser = userService.findByUsername(user.getUsername());
            fullUser.ifPresent(u -> session.setAttribute("userId", u.getId()));
            
            return "redirect:/";
        } else {
            model.addAttribute("error", "Credenciales inválidas");
            return "login";
        }
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute User user, Model model) {
        try {
            userService.registerNewUser(user);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
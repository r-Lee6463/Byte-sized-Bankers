package org.BSB.com.controller;


import org.BSB.com.dto.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FAQController {


    @GetMapping("/FAQ")
    public String showLoginPage(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "FAQ";
    }

}

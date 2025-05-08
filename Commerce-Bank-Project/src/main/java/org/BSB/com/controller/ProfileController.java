package org.BSB.com.controller;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.util.UUID;

import org.BSB.com.entity.User;
import org.BSB.com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserService userService;
    private final Path uploadDir = Paths.get("src/main/resources/static/uploads");

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal,
                          @RequestParam(required = false) String uploadError,
                          @RequestParam(required = false) String uploadSuccess) {
        User user = userService.findByEmail(principal.getName())
                               .orElseThrow();
        model.addAttribute("currentUser", user);
        model.addAttribute("uploadError",   uploadError);
        model.addAttribute("uploadSuccess", uploadSuccess);
        return "profile";
    }

    @PostMapping("/profile/upload")
    public String uploadProfileImage(@RequestParam("profileImage") MultipartFile file,
                                     Principal principal,
                                     RedirectAttributes ra) {
        if (file.isEmpty()) {
            ra.addAttribute("uploadError", "Please select a file to upload");
            return "redirect:/profile";
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            ra.addAttribute("uploadError", "File exceeds 5 MB limit");
            return "redirect:/profile";
        }

        User user = userService.findByEmail(principal.getName())
                               .orElseThrow();

        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            String filename = UUID.randomUUID() + ext;
            Path target = uploadDir.resolve(filename);
            file.transferTo(target);

            user.setProfileImage(filename);
            userService.saveUser(user);

            ra.addAttribute("uploadSuccess", "Profile picture updated!");
        } catch (IOException e) {
            ra.addAttribute("uploadError", "Upload failed: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}

package org.BSB.com.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.BSB.com.dto.AccountUpdateDto;
import org.BSB.com.entity.User;
import org.BSB.com.service.EmailExistsException;
import org.BSB.com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/account")
  public String showUpdateForm(Model model, Principal principal) {
    User current = userService.findByEmail(principal.getName()).orElseThrow();
    AccountUpdateDto dto = new AccountUpdateDto();
    dto.setEmail(current.getEmail());
    model.addAttribute("updateDto", dto);
    return "account";
  }

  @PostMapping("/account")
  public String processUpdate(
      @ModelAttribute("updateDto") @Valid AccountUpdateDto dto,
      BindingResult br,
      Principal principal
  ) {
    // 1) confirm password match
    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
      br.rejectValue("confirmPassword", "mismatch", "Passwords must match");
    }

    // 2) on errors, redisplay form
    if (br.hasErrors()) {
      return "account";
    }

    // 3) otherwise update and redirect with flag
    userService.updateUserAccount(principal.getName(), dto);
    return "redirect:/account?updated";
  }
}

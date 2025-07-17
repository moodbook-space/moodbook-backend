package org.com.moodbook.member.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

  @GetMapping("/login")
  public String login(Model model) {
    // 필요시 model 사용
    return "login";
  }

  @GetMapping("/signup")
  public String signup(Model model) {
    model.addAttribute("roles", List.of("USER", "ADMIN"));
    model.addAttribute("genders", List.of("MALE", "FEMALE", "OTHER"));
    model.addAttribute("statuses", List.of("ACTIVE", "DEACTIVATED"));
    return "signUp";
  }
}

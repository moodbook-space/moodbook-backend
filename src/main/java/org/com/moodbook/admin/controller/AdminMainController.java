package org.com.moodbook.admin.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminMainController {

  @GetMapping("/api/admin")
  public String getAdminMainPage() {

    return "admin/main";
  }

}

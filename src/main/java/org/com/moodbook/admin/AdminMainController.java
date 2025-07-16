package org.com.moodbook.admin;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMainController {

  //관리자 메인 페이지
  @GetMapping("")
  public String getAdminMainPage() {

    return "admin/main";
  }

  //관리자 채팅방 관리 페이지
  @GetMapping("/chat")
  public String chatPage() {
    return "admin/chat";
  }

  //관리자 유저 관리 페이지
  @GetMapping("/member")
  public String memberPage(){
    return "admin/member";
  }


}

package org.com.moodbook.security.core;


import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.constants.MemberStatus;
import org.com.moodbook.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class CustomMemberDetails implements UserDetails {

  private final Member member;

  @Override
  public String getPassword() {
    return member.getPassword();
  }

  public String getUserEmail() {
    return member.getEmail();
  }

  public MemberStatus getStatus(){return  member.getStatus();}

  @Override
  public String getUsername() {return member.getName();}

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    //Member의 권한을 변환하는 메서드
    //Collections.singleton <- 이 사용자는 한가지 권한만 갖는다는 의미
    //member.getRole().name()는 USER, ADMIN 등을 문자열로 꺼냄을 의미
    return Collections.singleton(new SimpleGrantedAuthority("ROLE_" +member.getRole().name()));

    //토큰에서 추출한 사용자 정보의 Id를 반환 (테이블의  pk 값)
    //User 엔티티에서 Id추출

  }
  //토큰에서 추출한 사용자 정보의 Id를 반환 (테이블의  pk 값)
  //User 엔티티에서 Id추출
  public Long getId(){
    return member.getId();
  }

  /** 아래는 현재 계정 상태를 판단하는 메서드 **/
  @Override  //현재 계정 상태가 활성화인지
  public boolean isEnabled() {
    return true;
  }

  @Override //이 계정이 만료되었는지
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override // 이 계정이 잠겨있는지
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override //자격증명이 만료되지 않았는지
  public boolean isCredentialsNonExpired() {
    return true;
  }
}

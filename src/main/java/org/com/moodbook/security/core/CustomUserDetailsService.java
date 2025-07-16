package org.com.moodbook.security.core;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final MemberRepository memberRepository;


  //로그인할 때 스프링에서 DB에 현재 로그인 하는 사용자가 존재하는지 확인하는 메서드
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 사용자가 없습니다"+email));

    return new CustomMemberDetails(member);
  }
  public UserDetails loadUserById(Long id) {
    Member member = memberRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("해당 회원이 존재하지 않습니다. id=" +id));
    return new CustomMemberDetails(member);

  }
}

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


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    return null;

  }
}

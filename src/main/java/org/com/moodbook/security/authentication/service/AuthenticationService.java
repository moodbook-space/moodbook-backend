package org.com.moodbook.security.authentication.service;

import org.com.moodbook.member.dto.LoginResponseDTO;
import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.entity.Member;

public interface AuthenticationService {

  LoginResponseDTO issueTokenAndStore(MemberDTO memberDTO);

}

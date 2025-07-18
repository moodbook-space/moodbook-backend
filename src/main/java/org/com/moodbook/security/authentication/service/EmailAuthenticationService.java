package org.com.moodbook.security.authentication.service;

public interface EmailAuthenticationService {

  void sendEmail(String toEmail);

  void verifyToken(String token);
}

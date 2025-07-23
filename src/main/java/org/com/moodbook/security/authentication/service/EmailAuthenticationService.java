package org.com.moodbook.security.authentication.service;

public interface EmailAuthenticationService {

  void sendEmail(String toEmail);

  void verifyToken(String token);

  void cleanupByExpiredToken(String expiredKey);

  void sendPasswordResetEmail(String email);

  void resetPassword(String token,String newPassword);
}

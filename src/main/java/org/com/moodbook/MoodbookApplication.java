package org.com.moodbook;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class MoodbookApplication {

  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().load();
    System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
    System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));

    SpringApplication.run(MoodbookApplication.class, args);
  }

}

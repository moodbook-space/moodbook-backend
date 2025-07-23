package org.com.moodbook;

import io.github.cdimascio.dotenv.Dotenv;
import org.com.moodbook.common.util.EnvInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class MoodbookApplication {

  public static void main(String[] args) {
    EnvInitializer.init();
    SpringApplication.run(MoodbookApplication.class, args);
  }

}

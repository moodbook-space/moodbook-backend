package org.com.moodbook.common.util;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvInitializer {

    public static void init() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

        setIfAbsent("MAIL_USERNAME", dotenv);
        setIfAbsent("MAIL_PASSWORD", dotenv);
    }

    private static void setIfAbsent(String key, Dotenv dotenv) {
        if (System.getenv(key) == null && System.getProperty(key) == null) {
            String value = dotenv.get(key);
            if (value != null) {
                System.setProperty(key, value);
            }
        }
    }
}

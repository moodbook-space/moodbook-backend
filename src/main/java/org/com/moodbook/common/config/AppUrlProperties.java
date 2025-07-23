package org.com.moodbook.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "moodbook.url")
public class AppUrlProperties {

    @Value("${moodbook.url.frontend}")
    private String frontend;

    @Value("${moodbook.url.backend}")
    private String backend;

}

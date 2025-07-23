package org.com.moodbook.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "moodbook.url")
public class AppUrlProperties {

    private String frontend;
    private String backend;

}

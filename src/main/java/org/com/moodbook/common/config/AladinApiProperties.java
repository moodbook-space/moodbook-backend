package org.com.moodbook.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aladin.api")
@Getter
@Setter
public class AladinApiProperties {
	private String key;
	private String baseUrl;
}

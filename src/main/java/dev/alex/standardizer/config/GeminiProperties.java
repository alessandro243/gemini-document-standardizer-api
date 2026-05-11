package dev.alex.standardizer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gemini.api")
@Getter
@Setter
public class GeminiProperties {
    private String key;
    private String url;
    private String username;
    private String password;
}

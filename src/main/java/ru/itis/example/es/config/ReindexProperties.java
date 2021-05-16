package ru.itis.example.es.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("reindex")
public class ReindexProperties {
    private Integer threadPoolSize = 4;
    private Integer rebuildPageSize = 1000;
    private Integer studentStartingPage = 0;
    private Integer studentSavedBefore = 0;
}

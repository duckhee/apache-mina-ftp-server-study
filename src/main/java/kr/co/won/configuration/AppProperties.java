package kr.co.won.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(value = "ftp")
public class AppProperties {

    private String rootPath = System.getProperty("user.dir")+"/temp/";
}

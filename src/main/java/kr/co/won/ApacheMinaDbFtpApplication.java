package kr.co.won;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApacheMinaDbFtpApplication {

    public static void main(String[] args) {
//        SpringApplication springApplication = new SpringApplication(ApacheMinaDbFtpApplication.class);
//        springApplication.setWebApplicationType(WebApplicationType.NONE);
//        springApplication.run(args);
        // service start
        SpringApplication.run(ApacheMinaDbFtpApplication.class, args);

    }

}

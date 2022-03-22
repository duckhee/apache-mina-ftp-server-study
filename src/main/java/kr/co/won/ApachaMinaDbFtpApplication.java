package kr.co.won;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

@SpringBootApplication
public class ApachaMinaDbFtpApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ApachaMinaDbFtpApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
        while (true) ;

    }

}

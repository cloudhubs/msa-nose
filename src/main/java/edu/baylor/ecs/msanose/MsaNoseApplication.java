package edu.baylor.ecs.msanose;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "edu.baylor.ecs")
public class MsaNoseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsaNoseApplication.class, args);
    }

}

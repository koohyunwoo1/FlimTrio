package com.example.flim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.flim")
public class FlimApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlimApplication.class, args);
    }

}

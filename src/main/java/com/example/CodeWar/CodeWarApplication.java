package com.example.CodeWar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin("*")
@EnableScheduling
public class CodeWarApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeWarApplication.class, args);
    }
}

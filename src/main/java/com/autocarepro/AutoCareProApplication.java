package com.autocarepro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AutoCareProApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoCareProApplication.class, args);
        System.out.println("\n==================================================");
        System.out.println("  AutoCare Pro is running: http://localhost:8080");
        System.out.println("==================================================\n");
    }
}

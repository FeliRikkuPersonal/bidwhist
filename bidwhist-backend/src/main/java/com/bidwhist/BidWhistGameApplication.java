package com.bidwhist;

// Import Spring Boot classes needed to run the application
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Marks this class as a Spring Boot application entry point
@SpringBootApplication
public class BidWhistGameApplication {

    // Main method: Entry point of the Java application
    public static void main(String[] args) {
        // Launches the Spring Boot application
        SpringApplication.run(BidWhistGameApplication.class, args);
    }

}

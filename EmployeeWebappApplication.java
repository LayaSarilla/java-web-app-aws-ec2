package com.laya.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Employee REST API web application.
 *
 * This application is designed to be packaged as a JAR (mvn clean package)
 * and deployed on an AWS EC2 Linux instance, connecting to a MySQL database
 * (hosted either on the same EC2 instance or on AWS RDS).
 */
@SpringBootApplication
public class EmployeeWebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeWebappApplication.class, args);
    }
}

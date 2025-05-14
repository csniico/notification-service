package com.csniico.notificationService.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    public static final String TOPIC_ARN;

    static {
        String arn = System.getenv("SNS_TOPIC_ARN");

        if (arn == null) {
            System.err.println("Warning/Error: MY_NOTIFICATION_TOPIC_ARN not set.");
            throw new RuntimeException("SNS_TOPIC_ARN not set.");
        }
        else {
            TOPIC_ARN = arn;
        }
        System.out.println("SNS_TOPIC_ARN: " + TOPIC_ARN);
    }
}

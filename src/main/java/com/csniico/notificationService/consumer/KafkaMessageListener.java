package com.csniico.notificationService.consumer;

import com.csniico.notificationService.SNS.SNSClient;
import com.csniico.notificationService.SNS.UseMessageFilterPolicy;
import com.csniico.notificationService.config.AppConfig;
import com.csniico.notificationService.dto.Task;
import com.csniico.notificationService.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

import java.util.*;

@Service
public class KafkaMessageListener {
    Logger logger = LoggerFactory.getLogger(KafkaMessageListener.class);
    List<String> emails = new ArrayList<>();
    SNSClient _snsClient;
    SnsClient snsClient = SnsClient.builder()
            .build();

    @KafkaListener(topics = "task.created")
    public void taskCreatedConsumer(@Payload Task task) {
        try{
            logger.info("consumer consumed the message {}", task.toString());
            emails.addAll(Arrays.asList(task.getAssignedTo()));
            emails.forEach(email -> {
                System.out.printf("Sending email to %s%n", email);
                Map<String, MessageAttributeValue> attributes = new HashMap<>();
                attributes.put("target_email", MessageAttributeValue.builder().dataType("String").stringValue(email).build());
                String message = String.format("Task %s has been assigned to you", task.getTitle());
                SNSClient.pubTopicWithAttributes(snsClient, AppConfig.TOPIC_ARN, message, attributes);
                System.out.println("Email notification sent to " + email);
            });
        } catch (RuntimeException e) {
            System.out.printf("Error while consuming message: %s%n", e.getMessage());
        }
    }

    @KafkaListener(topics = "user.created")
    public void userCreatedConsumer(@Payload User user) {
        System.out.println("Received from DLT: " + user);
        emails.add(user.getEmail());
        emails.forEach(email -> {
            String subscriptionArn = SNSClient.SubScribeToTopic(snsClient, AppConfig.TOPIC_ARN, email);
            UseMessageFilterPolicy.usePolicyWithEmail(snsClient, subscriptionArn, email);
            System.out.println("SubscriptionArn for "+ "email" + " is "+ subscriptionArn);
        });
    }
}

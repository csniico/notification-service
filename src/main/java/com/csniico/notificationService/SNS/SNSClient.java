package com.csniico.notificationService.SNS;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.Map;

public class SNSClient {

    public static String CreateSNSTopic(SnsClient snsClient, String topicName) {
        CreateTopicResponse result = null;
        try{
            CreateTopicRequest request = CreateTopicRequest.builder()
                    .name(topicName)
                    .build();

            result = snsClient.createTopic(request);
            System.out.println("Created SNS Topic: " + result.topicArn());
            return result.topicArn();
        } catch(SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    public static String SubScribeToTopic(SnsClient snsClient, String topicArn, String email) {
        try {
            SubscribeRequest request = SubscribeRequest.builder()
                    .protocol("email")
                    .endpoint(email)
                    .returnSubscriptionArn(true)
                    .topicArn(topicArn)
                    .build();

            SubscribeResponse result = snsClient.subscribe(request);
            System.out.println("Subscription ARN: " + result.subscriptionArn() + "\n\n Status is " + result.sdkHttpResponse().statusCode());
            return result.subscriptionArn();
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    public static void pubTopic(SnsClient snsClient, String message, String topicArn) {

        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .topicArn(topicArn)
                    .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    public static void pubTopicWithAttributes(SnsClient snsClient, String topicArn, String message, Map<String, MessageAttributeValue> messageAttributes) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .topicArn(topicArn)
                    .messageAttributes(messageAttributes)
                    .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent WITH ATTRIBUTES. Status is " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println("Error publishing to topic with attributes: " + e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

}

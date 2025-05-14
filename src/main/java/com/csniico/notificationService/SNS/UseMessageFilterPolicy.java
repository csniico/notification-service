package com.csniico.notificationService.SNS;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SnsException;
import java.util.ArrayList;

public class UseMessageFilterPolicy {

    public static void usePolicyWithEmail(SnsClient snsClient, String subscriptionArn, String email) {
        try{
            SNSMessageFilterPolicy fp = new SNSMessageFilterPolicy();

            fp.addAttribute("target_email", email);
            fp.apply(snsClient, subscriptionArn);

            System.out.println("Added email filter policy to subscription: " + subscriptionArn);
        }catch (SnsException ex) {
            System.err.println(ex.awsErrorDetails().errorMessage());
            throw ex;
        }
    }
}

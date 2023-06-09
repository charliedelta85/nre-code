package com.readgroup.nreclient;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import software.amazon.awssdk.services.sqs.endpoints.SqsEndpointParams;
import software.amazon.awssdk.services.sqs.endpoints.SqsEndpointProvider;
import software.amazon.awssdk.services.sqs.endpoints.internal.DefaultSqsEndpointProvider;
import software.amazon.awssdk.services.sqs.model.*;

import software.amazon.awssdk.auth.credentials.*;
public class QueueUtils {

    final private String queueUrl;
    SqsClient sqsClient;


    public QueueUtils(String queueName, String vpcEndpoint, String region){
        SqsEndpointProvider endpointProvider = new DefaultSqsEndpointProvider();
        SqsEndpointParams endpointParams = SqsEndpointParams.builder().endpoint(vpcEndpoint).region(Region.of(region)).build();
        endpointProvider.resolveEndpoint(endpointParams);
        System.out.println(queueName);
        GetQueueUrlRequest queueUrlRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();
        sqsClient =  SqsClient.builder()
                .region(Region.of(region))
                .endpointProvider(endpointProvider)
                .build();
        queueUrl = sqsClient.getQueueUrl(queueUrlRequest).queueUrl();
        System.out.println(queueUrl);
     //    queueUrl = "https://sqs.eu-west-1.amazonaws.com/065179189732/RailUpdatesStompMessages";
    }

    public void sendMessage(String message){
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .delaySeconds(5)
                .build();
        System.out.println("message about to ");

        try {
            sqsClient.sendMessage(sendMsgRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("message sent");

    }

}

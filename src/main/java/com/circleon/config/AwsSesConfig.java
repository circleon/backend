package com.circleon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class AwsSesConfig {

    @Value("${aws.ses.access.key.id}")
    private String awsSesAccessKeyId;

    @Value("${aws.ses.secret.access.key}")
    private String awsSesSecretAccessKey;

    @Bean
    public SesClient sesClient(){
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(awsSesAccessKeyId, awsSesSecretAccessKey);

        return SesClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }
}

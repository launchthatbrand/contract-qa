package com.desmondtatilian.contractqa;

import com.desmondtatilian.contractqa.ai.RetrievalProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RetrievalProperties.class)
public class ContractQaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractQaApplication.class, args);
    }
}

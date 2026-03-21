package com.hackathon.supplychainrisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SupplyChainRiskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainRiskApplication.class, args);
    }
}

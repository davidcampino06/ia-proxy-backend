package com.ucc.iaproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class IaProxyApplication {

    public static void main(String[] args) {
        // Time zone for the daily history and the monthly reset (configurable with APP_TIMEZONE)
        String zone = System.getenv().getOrDefault("APP_TIMEZONE", "America/Bogota");
        TimeZone.setDefault(TimeZone.getTimeZone(zone));
        SpringApplication.run(IaProxyApplication.class, args);
    }
}

package com.ctrip.apollo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SampleConfigServiceApplication {
  public static void main(String[] args) {
    new SpringApplicationBuilder(SampleConfigServiceApplication.class).run(args);
  }
}

package me.xhy.java.springcloud.s2.provider.movie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/*
@EnableEurekaClient 表明是 eureka client
 */
@SpringBootApplication
@EnableEurekaClient
public class MovieProviderApplication {
  public static void main(String[] args) {
    SpringApplication.run(MovieProviderApplication.class);
  }
}
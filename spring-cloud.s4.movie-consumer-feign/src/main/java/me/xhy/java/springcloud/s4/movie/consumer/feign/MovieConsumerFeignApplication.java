package me.xhy.java.springcloud.s4.movie.consumer.feign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/*
@EnableFeignClients 开启 s2feign
 */
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
public class MovieConsumerFeignApplication {
  public static void main(String[] args) {
    SpringApplication.run(MovieConsumerFeignApplication.class, args);
  }
}

package me.xhy.java.springcloud.s1.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/*
1. @SpringBootApplication
spring-boot 注解，相当于 @Configuration + @EnableAutoConfiguration + @ComponentScan
@EnableAutoConfiguration -> AutoConfigurationImportSelector  会加载 META-INF/spring.factories 中指定的组件

2. @EnableEurekaServer
完成注册中心功能
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
  public static void main(String[] args) {
    /*
    启动参数一定要加 `args` ，以后命令行传参就是靠这个变量
     */
    SpringApplication.run(EurekaServerApplication.class, args);
  }
}
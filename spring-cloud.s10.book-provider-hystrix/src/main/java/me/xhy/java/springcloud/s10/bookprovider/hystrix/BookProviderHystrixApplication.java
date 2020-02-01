package me.xhy.java.springcloud.s10.bookprovider.hystrix;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class BookProviderHystrixApplication {
  public static void main(String[] args) {
    SpringApplication.run(BookProviderHystrixApplication.class, args);
  }

  /*
  高版本 spring boot（大概是1.3.x 以后吧） 需要手动实例化 HystrixMetricsStreamServlet
   */
  @Bean
  public ServletRegistrationBean getServlet(){
    HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();    //监控实例
    ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);    //servlet注册接口
    registrationBean.setLoadOnStartup(1);
    registrationBean.addUrlMappings("/actuator/hystrix.stream");   //路径
    registrationBean.setName("HystrixMetricsStreamServlet");
    return registrationBean;
  }
}

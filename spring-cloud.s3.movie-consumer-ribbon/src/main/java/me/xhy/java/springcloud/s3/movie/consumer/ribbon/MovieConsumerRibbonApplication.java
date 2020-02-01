package me.xhy.java.springcloud.s3.movie.consumer.ribbon;

import com.netflix.loadbalancer.*;
import me.xhy.java.springcloud.s3.movie.consumer.ribbon.config.IgnoreScan;
import me.xhy.java.springcloud.s3.movie.consumer.ribbon.config.RibbonPartialStrategyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;

/*
@ComponentScan 应用 IgnoreScan ， 排除我们定义的局部负载均衡策略
@RibbonClient 针对 client 指定负载均衡策略。 name 是请求服务提供方的 ${spring.application.name} ，configuration 是该客户端应用各种策略
 */
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = IgnoreScan.class))
@RibbonClient(name = "movie-provider", configuration = RibbonPartialStrategyConfig.class)
public class MovieConsumerRibbonApplication {
  public static void main(String[] args) {
    SpringApplication.run(MovieConsumerRibbonApplication.class, args);
  }

  @Bean
  @LoadBalanced
  RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public IRule ribbonRule() {
    return new
        RandomRule() // 随机分配
        //RoundRobinRule(); // 轮询
        //RetryRule(); // 重试
        //BestAvailableRule() // 最低并发
        //AvailabilityFilteringRule() // 可用过滤
        //ResponseTimeWeightedRule() // 响应时间加权
        //ZoneAvoidanceRule() // 区域权衡
        ;
  }
}

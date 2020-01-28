package me.xhy.java.springcloud.s3.movie.consumer.ribbon.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.context.annotation.Bean;

@IgnoreScan
public class RibbonPartialStrategyConfig {
  @Bean
  public IRule ribbonRule() {
    return new RoundRobinRule(); // 轮训
  }
}

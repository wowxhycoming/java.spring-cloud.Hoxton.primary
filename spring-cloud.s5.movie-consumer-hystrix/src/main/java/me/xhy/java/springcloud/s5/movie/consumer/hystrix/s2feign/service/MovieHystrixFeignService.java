package me.xhy.java.springcloud.s5.movie.consumer.hystrix.s2feign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/*
Feign 的熔断，只需要在原有注解中，增加 fallback 属性，该属性需要一个 class 类型值
传入的 class 需要实现接口中的方法，供资源部可达时使用，
 */
@FeignClient(value = "movie-provider", fallback = FallbackGetMovies.class)
public interface MovieHystrixFeignService {

  @RequestMapping("/movie/movies")
  public String getMovies();

}

/*
该类提供资源部可达时的补偿方法
必须要有 @Component ， 可以是外部类
 */
@Component
class FallbackGetMovies implements MovieHystrixFeignService{
  @Override
  public String getMovies() {
    return "feign method getMovies occur hystrix";
  }
}


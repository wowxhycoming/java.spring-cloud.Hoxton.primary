package me.xhy.java.springcloud.s5.movie.consumer.hystrix.s1ribbon.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/consumer/hystrix/ribbon/movie")
public class MovieHystrixRibbonController {

  @Autowired
  RestTemplate restTemplate;

  /*
    新增了 @HystrixCommand
    @HystrixCommand(fallbackMethod = "getMoviesError") 默认键为 fallbackMethod
    表示当调用 provider ，调用的补偿（快速失败）方法
    默认情况下，发生3次错误（补偿方法调用了3次），该 provider 将被熔断
    当被熔断的 provider 重新上线，将被重新加回到服务列表
  */
  @RequestMapping("movies")
  @HystrixCommand(fallbackMethod = "getMoviesError")
  public String getMovies() {
    String GET_MOVIES = "http://movie-provider/movie/movies";
    return "ribbon got " + restTemplate.getForObject(GET_MOVIES, String.class);
  }

  public String getMoviesError() {
    return "ribbon method getMovies occur hystrix";
  }

}

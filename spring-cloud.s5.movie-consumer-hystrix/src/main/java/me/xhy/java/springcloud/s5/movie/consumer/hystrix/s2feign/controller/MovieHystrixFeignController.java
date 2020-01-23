package me.xhy.java.springcloud.s5.movie.consumer.hystrix.s2feign.controller;

import me.xhy.java.springcloud.s5.movie.consumer.hystrix.s2feign.service.MovieHystrixFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumer/hystrix/feign/movie")
public class MovieHystrixFeignController {

  @Autowired
  MovieHystrixFeignService movieHystrixFeignService;

  @RequestMapping("movies")
  public String getMovies() {
    return movieHystrixFeignService.getMovies();
  }
}

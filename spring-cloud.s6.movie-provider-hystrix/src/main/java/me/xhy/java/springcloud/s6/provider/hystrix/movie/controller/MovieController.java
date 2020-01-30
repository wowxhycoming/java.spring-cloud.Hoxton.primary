package me.xhy.java.springcloud.s6.provider.hystrix.movie.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import me.xhy.java.springcloud.s6.provider.hystrix.config.ServerPortConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("movie")
public class MovieController {

  @Autowired
  ServerPortConfiguration serverPortConfiguration;

  @RequestMapping("movies")
  @HystrixCommand(fallbackMethod = "getMoviesFallback")
  public String getMovies() {
    if(new Random().nextInt(10) / 2 == 0) {
      throw new RuntimeException("2");
    }
    return "movies" + serverPortConfiguration.getPort();
  }

  public String getMoviesFallback() {
    return "服务降级 " + serverPortConfiguration.getPort();
  }
}

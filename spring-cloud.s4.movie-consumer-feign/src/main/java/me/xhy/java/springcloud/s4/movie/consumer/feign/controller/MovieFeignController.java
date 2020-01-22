package me.xhy.java.springcloud.s4.movie.consumer.feign.controller;

import me.xhy.java.springcloud.s4.movie.consumer.feign.service.MovieFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumer/feign/movie")
public class MovieFeignController {

  /*
  引入刚写好的接口。这里不再试 Ribbon 的 RestTemplate 。
  较新版本的 Intellij 不会再报无法注入、找不到匹配类型的 编译 错误了
   */
  @Autowired
  MovieFeignService movieFeignService;

  @RequestMapping("movies")
  public String getMovies() {
    return movieFeignService.getMovies();
  }
}

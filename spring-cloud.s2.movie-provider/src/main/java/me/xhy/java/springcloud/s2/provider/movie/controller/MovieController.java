package me.xhy.java.springcloud.s2.provider.movie.controller;

import me.xhy.java.springcloud.s2.provider.config.ServerPortConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
@RestController = @ResponseBody +  @Controller
该视图解析器直接返回字符串
 */
@RestController
@RequestMapping("movie")
public class MovieController {

  @Autowired
  ServerPortConfiguration serverPortConfiguration;

  @RequestMapping("movies")
  public String getMovies() {
    return "movies" + serverPortConfiguration.getPort();
  }
}

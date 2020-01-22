package me.xhy.java.springcloud.s3.movie.consumer.ribbon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("movie")
public class MovieController {

  @Autowired
  RestTemplate restTemplate;

  @RequestMapping("movies")
  public String getMovies() {
    // 使用 服务名 代替BASE URL ，后面正常接 资源路径
    String GET_MOVIES = "http://movie-provider/movie/movies";
    return GET_MOVIES + " == " + restTemplate.getForObject(GET_MOVIES, String.class);
  }
}

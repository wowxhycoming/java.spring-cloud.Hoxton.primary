package me.xhy.java.springcloud.s4.movie.consumer.feign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
@FeignClient("") 参数填 Spring Cloud Provider 的应用名 ${spring.application.name}
回想一下 Ribbon 放问资源的 Http地址： String GET_MOVIES = "http://movie-provider/movie/movies";
 */
@FeignClient("movie-provider")
public interface MovieFeignService {

  /*
  @RequestMapping() 默认参数是请求资源的地址，
  也可以指定请求的 Http Method @RequestMapping(value="/movie/movies",method=RequestMethod.GET)

  getMovies() 也可以算入参数，例如 getMovies(@RequestParam("name") String name) ，调用时比如传入的参数
   */
  @RequestMapping("/movie/movies")
  public String getMovies();
}

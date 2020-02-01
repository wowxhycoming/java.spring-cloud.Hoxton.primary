package me.xhy.java.springcloud.s11.primary.consumer.zuul.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/*
访问 movie 和 book 两个服务，这里只需要一个 Feign 客户端
 */
@FeignClient(name = "primary-zuul")
public interface PrimaryHystrixOrientedZuulService {

  @RequestMapping("/movie-proxy1/movie/movies")
  String getMovies();

  @RequestMapping("/book-proxy/book/books")
  String getBooks();
}
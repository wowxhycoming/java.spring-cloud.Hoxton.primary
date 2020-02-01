package me.xhy.java.springcloud.s10.bookprovider.hystrix.book.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import me.xhy.java.springcloud.s10.bookprovider.hystrix.config.ServerPortConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("book")
public class BookController {

  @Autowired
  ServerPortConfiguration serverPortConfiguration;

  @RequestMapping("books")
  @HystrixCommand(fallbackMethod = "getBooksFallback")
  public String getMovies() {
    if(new Random().nextInt(10) / 2 == 0) {
      throw new RuntimeException("2");
    }
    return "books" + serverPortConfiguration.getPort();
  }

  public String getBooksFallback() {
    return "books 服务降级 " + serverPortConfiguration.getPort();
  }
}
package me.xhy.java.springcloud.s11.primary.consumer.zuul.controller;

import me.xhy.java.springcloud.s11.primary.consumer.zuul.service.PrimaryHystrixOrientedZuulService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("primary")
public class PrimaryConsumerController {

  @Autowired
  PrimaryHystrixOrientedZuulService service;

  @RequestMapping("allStuff")
  public String getAllStuff() {
    return service.getMovies() + " | " +service.getBooks();
  }
}

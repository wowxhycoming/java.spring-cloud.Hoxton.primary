package me.xhy.java.springcloud.s10.bookprovider.hystrix.config;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ServerPortConfiguration implements ApplicationListener<WebServerInitializedEvent> {

  private int serverPort;

  @Override
  public void onApplicationEvent(WebServerInitializedEvent event) {
    this.serverPort = event.getWebServer().getPort();
  }

  public int getPort() {
    return this.serverPort;
  }

}
package me.xhy.java.springcloud.s9.primary.zuul.fallback;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class StuffFallbackProvider implements FallbackProvider {

  /*
  该方法用于匹配路由，匹配的是 zuul 代理的服务，就是配置文件里路由表里 serviceId: 后面的值。
  "*" 表示匹配任何路由
  "movie-provider" 就只匹配一个服务了
   */
  @Override
  public String getRoute() {
    return "*";
  }

  @Override
  public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
    return new ClientHttpResponse() {

      /*
      响应头
       */
      @Override
      public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "text/html; charset=UTF-8");
        return httpHeaders;
      }

      /*
      响应体
       */
      @Override
      public InputStream getBody() throws IOException {
        return new ByteArrayInputStream("服务不可用".getBytes());
      }

      @Override
      public HttpStatus getStatusCode() throws IOException {
        return HttpStatus.BAD_REQUEST;
      }

      @Override
      public int getRawStatusCode() throws IOException {
        return HttpStatus.BAD_REQUEST.value();
      }

      @Override
      public String getStatusText() throws IOException {
        return HttpStatus.BAD_REQUEST.getReasonPhrase();
      }

      @Override
      public void close() {

      }
    };
  }
}

# 一、开始

1. 鉴于目前对微服务的认知，按大致如下顺序展开： 注册中心、 provider 、 consumer 、 集群、 负载均衡、 容错。
2. 本项目使用 spring-cloud 的版本为 Hoxton.SR1， 当前时间 2020.01.18 。
3. 基于 spring-boot 构建项目， 查看 spring-cloud 和 spring-boot 版本的对应 https://start.spring.io/actuator/info
4. jdk version 1.8
5. 项目 和 子模块均是 maven 项目。
6. 在已经知道配置文件重要性的前提下，尽量少埋伏笔，给出目前最全的配置文件内容
7. 按 consumer -\> register center -\> provider 分配端口区域 33000 -\> 35000 -\> 37000
8. IDE 为 Intellij IDEA

# 二、创建根项目

1. 新建 maven 项目： java.spring-cloud.Hoxton

2. pom 文件添加 properties
```
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
  </properties>
```

2. 修改 pom 文件，按官网的描述：
```
Release Train Version: Hoxton.RELEASE
Supported Boot Version: 2.2.1.RELEASE
```
确定版本，如下：

```
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-dependencies</artifactId>
        <version>Hoxton.RELEASE</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>2.2.1.RELEASE</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
```

> maven 与 java 在继承方面的定义一样，都是单继承。单独使用 spring-boot 时候，可以使用 parent 标签继承 spring-boot 的 pom 文件，但是想继承多个 pom 是不可能的。 <scope>import</scope> 用于解决这个问题。

> 另外，根项目只需要 使用 dependencyManagement 预定义 dependencies 即可，严禁使用 dependencies ，此时项目下 lib 库仍然没包含任何 jar。子项目按需引入。


# 三、创建注册中心 eureka-server

本项目将使用伪集群方式部署 eureka-server 。

1. 模块名称 spring-cloud.s1.eureka-server (s1 表示 step1)

2. 修改 pom 文件
```
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
  </dependencies>
```

3. 创建启动类 me.xhy.java.springcloud.s1.eureka.EurekaServer

```
/*
1. @SpringBootApplication
spring-boot 注解，相当于 @Configuration + @EnableAutoConfiguration + @ComponentScan
@EnableAutoConfiguration -> AutoConfigurationImportSelector  会加载 META-INF/spring.factories 中指定的组件
这里应用了 SPI 协议

2. @EnableEurekaServer
完成注册中心功能
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(EurekaServerApplication.class);
  }
}
```

4. 添加配置文件

eureka是一个高可用的组件，它没有后端缓存，每一个实例注册之后需要向注册中心发送心跳（因此可以在内存中完成），在默认情况下erureka server也是一个eureka client ,必须要指定一个 server。eureka server的配置文件application.yml：

```
server:
  port: 35001

eureka:
  instance:
    hostname: localhost

spring:
  application:
    name: eurka-server
```

通过 eureka.client.registerWithEureka: false 和 fetchRegistry: false 来表明自己是一个eureka server.


5. 启动工程

运行 EurekaServerApplication 的 main 函数。  

此时会报错，先忽略。可以从控制台启动日志看到，tomcat 被启动了，端口是配置文件指定的 32001 。 

6. 访问

访问 http://localhost:35001/ 。

此时还没有服务注册到 eureka ，"Instances currently registered with Eureka" 区域显示 "No instances available" 。
 
# 四、创建服务提供者实现项目

服务提供者、服务消费者，都是 eureka client。  
当 client 向 server 注册时，它会提供一些元数据，例如主机和端口，URL，主页等。  
Eureka server 从每个 client 实例接收心跳消息。  
如果心跳超时，则通常将该实例从注册 server 中删除。  

## 创建模块

1. 模块名称 spring-cloud.s2.provider-movie

2. 修改 pom
```
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
  </dependencies>
```

这里是 eureka-client，不再是 server。  
引入了 web 功能，因为基于 spring cloud 的微服务就是建立在 http 协议上的。

3. 创建启动类，并声明是 eureka client
```
/*
@EnableEurekaClient 表明是 eureka client 
 */
@SpringBootApplication
@EnableEurekaClient
public class MovieProviderApplication {
  public static void main(String[] args) {
    SpringApplication.run(MovieProviderApplication.class);
  }
}
```

4. 创建配置文件，指定注册中心地址
```
server:
  port: 10002
spring:
  application:
    name: provider-movie
eureka:
  client:
    service-url:
      defaultZone: http://localhost:35001/eureka
  instance:
    # 应用名 会影响消费者调用，使用spring.application.name
    # appname: provider-movie
    # 应用列表中 每个实例的名字
    instance-id: ${spring.cloud.client.ip-address}:${server.port}/${spring.application.name}
    # 鼠标放到应用列表的实例上，状态类中的地址信息指定为ip
    prefer-ip-address: true
```

> spring.application.name 的设置是为了让 "Application" 处可以正常显示应用名，而不是 UNKNOWN。
> eureka.instance.instance-id 的设置是让 "Status" 出可以正常显示
> eureka.instance.prefer-ip-address 的设置是让 "Status" 的链接已真实IP显示，而不是 localhost 

5. 启动 provider 

运行 MovieProviderApplication 的 main 函数。

6. 再看注册中心 web 页面

(1). 关于： EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.  

这是因为Eureka进入了自我保护机制，默认情况下，如果EurekaServer在一定时间内没有接收到某个微服务实例的心跳时，EurekaServer将会注销该实例（默认90s）。
但是当网络发生故障时，微服务与EurekaServer之间无法通信，这样就会很危险了，因为微服务本身是很健康的，
此时就不应该注销这个微服务，而Eureka通过自我保护机制来预防这种情况，当网络健康后，该EurekaServer节点就会自动退出自我保护模式；
说白一点：当 eureka 以为服务掉线的时候，不会立刻下线改服务，仍然会在服务列表中保留该服务，直到90还没有心跳发生，再移除。

这时再次将客户端微服务启动，刷新服务注册中心会发现，自我保护状态已取消。

综上所述，我们可以看出来Eureka的两个组件EurekaServer和EurekaClient的作用：

- EurekaServer 提供服务发现的能力，各个微服务启动时，会向EurekaServer注册自己的信息（例如：ip、端口、微服务名称等），EurekaServer会存储这些信息；
- EurekaClient是一个Java客户端，用于简化与EurekaServer的交互；
- 微服务启动后，会定期性（默认30s）的向EurekaServer发送心跳以续约自己的“租期”；
- 如果EurekaServer在一定时间内未接收某个微服务实例的心跳，EurekaServer将会注销该实例（默认90s）；
- 默认情况下，EurekaServer同时也是EurekaClient。多个EurekaServer实例，互相之间通过复制的方式，来实现服务注册表中数据的同步；
- EurekaClient也会缓存服务注册表中的信息；

综上，Eureka通过心跳检查、客户端缓存等机制，提高了系统的灵活性、可伸缩性和可用性，所以作为一个微服务架构，需要一个服务注册中心来统筹管理服务；

(2). 在 "Instances currently registered with Eureka" 区域可以显示注册上来的服务了。

(3). 在 "Instances currently registered with Eureka" 的 status 处的链接 http://ip:37001/actuator/info ，这个 `/actuator/info` 是 spring-boot 的监控组件。


## 添加 actuator 

1. 修改 pom 文件，添加依赖
```
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
```

2. 修改配置文件，添加监控信息
```
info:
  app.name: provider-movie
  compony.name: me.xhy
  build.artifactId: $project.artifactId$
  build.modelVersion: $project.modelVersion$
```

此处的 `$` 暂时无法识别，会被当成普通字符串处理，添加一个 maven 插件来处理

3. 修改根项目 pom 文件，添加插件
```
  <build>
    <finalName>java.spring-cloud.Hoxton</finalName>
    <resources>
      <resource>
        <directory>src/main/resources</directory>
        <filtering>true</filtering>
      </resource>
    </resources>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-resources-plugin</artifactId>
        <configuration>
          <delimiters>
            <delimiter>$</delimiter>
          </delimiters>
        </configuration>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
          <source>${java.version}</source><!-- 源代码使用的开发版本 -->
          <target>${java.version}</target><!-- 需要生成的目标class文件的编译版本 -->
        </configuration>
      </plugin>
    </plugins>
  </build>
```

重新运行，再访问。

> 访问 http://ip:37001/actuator/info 会提示下载文件，因为返回的是个流，用 google 浏览器可以适配。

# 五、调整 客户端-注册中心 反馈时间

## 前情回顾 场景制造
1. 在 三.5 章节的报错信息
是因为没有配置高可用造成的。
eureka-server 想获取其他服务实例，也想将自己注册到 eureka-server 上，发现不可行。

2. 此时将唯一的 provider 下线，刷新 eureka 管理端，发现服务仍然存在于列表中。上面说了，是 eureka 的保护机制。

## 消除假象

1. 修改 eureka-server 配置文件， 在 `eureka` 节点下增加  节点
```
eureka:
  client:
    registerWithEureka: false # 是否从 eureka 获取信息
    fetchRegistry: false # 是否注册到 eureka
  server:
    eviction-interval-timer-in-ms: 1000 # 设置清理的时间间隔，单位是 ms ，默认值 60000ms(60s)
```

2. client 端增加心跳检查，修改配置文件，在 `eureka.instance` 下，增加节点
```
    lease-renewal-interval-in-seconds: 2 # 设置心跳时间间隔，默认30s
    lease-expiration-duration-in-seconds: 5 # 超过5s没有心跳，开始清理，默认90s
```
2s 一个心跳，5s 清理服务列表明显不合理，这里只是体验一下。看下心跳的debug日志，然后注释掉。

## 关于 eureka

还有 注册中心高可用、安全机制 等。目前先到这里，以后在合适的场景补充。

# 六、Ribbon 和 RestTemplate

## RestTemplate

是 spring 提供的一种简单便捷的模板类来进行 Http 操作

## 关于Ribbon 

1. 是一个 http、tcp 负载均衡
2. 需要连接到注册中心，下载服务列表到本地，之后才可负载均衡
3. eureka-client 依赖了 ribbon ，所以项目不需要显示引用 ribbon


## ribbon 的使用

添加 eureka-client 依赖 -> 配置注册中心 -> 启动类添加 eureka-client注解 -> RestTemplate 头顶 @Balance




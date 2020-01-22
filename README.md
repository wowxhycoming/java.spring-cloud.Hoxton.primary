# 一、开始

1. 鉴于目前对微服务的认知，按大致如下顺序展开： 注册中心、 provider 、 consumer 、 集群、 负载均衡、 容错。
2. 本项目使用 spring-cloud 的版本为 Hoxton.RELEASE， 当前时间 2020.01.18 。
3. 基于 spring-boot 构建项目， 查看 spring-cloud 和 spring-boot 版本的对应 https://start.spring.io/actuator/info
4. jdk version 1.8
5. 项目 和 子模块均是 maven 项目。
6. 在已经知道配置文件重要性的前提下，尽量少埋伏笔，给出目前最全的配置文件内容
7. 按 consumer -\> register center -\> provider 分配端口区域 33000 -\> 35000 -\> 37000
8. IDE 为 Intellij IDEA

# 二、创建根项目

1. 新建 maven 项目： `java.spring-cloud.Hoxton`

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
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.1.RELEASE</version>
    <relativePath/>
  </parent>
  
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-dependencies</artifactId>
        <version>Hoxton.RELEASE</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
  </dependencyManagement>
```

> maven 与 java 在继承方面的定义一样，都是单继承。单独使用 spring-boot 时候，可以使用 parent 标签继承 spring-boot 的 pom 文件，但是想继承多个 pom 是不可能的。 <scope>import</scope> 用于解决这个问题。

> 这里采用继承 `spring-boot-starter-parent` ，引入 `spring-cloud-dependencies` 。 `spring-boot-starter-parent` 中有很多写好的插件、构建等可以直接拿来用，因为 spring cloud 也是基于 spring boot 封箱的。 可以跟踪到引入的 pom 中看看都有什么。

> 另外，根项目只需要 使用 dependencyManagement 预定义 dependencies 即可，严禁使用 dependencies ，此时项目下 lib 库仍然没包含任何 jar。子项目按需引入。


# 三、创建注册中心 eureka-server

本项目将使用伪集群方式部署 eureka-server 。

1. 模块名称 `spring-cloud.s1.eureka-server` (s1 表示 step1)

2. 修改 pom 文件
```
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
  </dependencies>
```

3. 创建启动类 `me.xhy.java.springcloud.s1.eureka.EurekaServer`

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
    /*
    启动参数一定要加 `args` ，以后命令行传参就是靠这个变量
     */
    SpringApplication.run(EurekaServerApplication.class, args);
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

通过 `eureka.client.registerWithEureka`: false 不向服务中心注册 和 `eureka.client.fetchRegistry`: false 不获取服务列表，来表明自己是一个 eureka server.

5. 启动工程

运行 EurekaServerApplication 的 main 函数。  

此时会报错，先忽略。可以从控制台启动日志看到，tomcat 被启动了，端口是配置文件指定的 32001 。 

6. 访问

访问 `http://localhost:35001/` 。

> 可不是 `http://localhost:35001/eureka`

此时还没有服务注册到 eureka ，"Instances currently registered with Eureka" 区域显示 "No instances available" 。
 
# 四、创建服务提供者实现项目

服务提供者、服务消费者，都是 eureka client。  
当 client 向 server 注册时，它会提供一些元数据，例如主机和端口，URL，主页等。  
Eureka server 从每个 client 实例接收心跳消息。  
如果心跳超时，则通常将该实例从注册 server 中删除。  

## 创建模块

1. 模块名称 `spring-cloud.s2.movie-provider`

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
  port: 37001 # 服务端口
spring:
  application:
    name: movie-provider # 应用名称
eureka:
  client:
    service-url:
      defaultZone: http://localhost:35001/eureka
  instance:
    instance-id: ${spring.cloud.client.ip-address}:${server.port}/${spring.application.name} # 代表了一个启动示例的标识，自定义，可以显示在控制台上，
    prefer-ip-address: true # 调用服务的时候使用 IP 优先，而不是使用域名。鼠标放到应用列表的实例上，状态类中的地址信息指定为 ip。
    lease-renewal-interval-in-seconds: 30 # 表示 eureka client 发送心跳给server端的频率。这个值决定了服务注册的快慢，太快消耗资源。默认30秒。
    lease-expiration-duration-in-seconds: 90 # 表示 eureka server 至上一次收到 client 的心跳之后，等待下一次心跳的超时时间，在这个时间内若没收到下一次心跳，则将移除该 instance。默认90秒
info:
  app.name: movie-provider
  compony.name: me.xhy
  build.artifactId: $project.artifactId$
  build.modelVersion: $project.modelVersion$
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
    <resources>
      <resource>
        <directory>src/main/resources</directory>
        <filtering>true</filtering>
        <excludes>
          <exclude>*.bat</exclude>
        </excludes>
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

## 服务提供者还没有实质性的服务，添加一个

1. 创建业务类 `..MovieController.java` 

```
/*
@RestController = @ResponseBody +  @Controller
该视图解析器直接返回字符串
 */
@RestController
@RequestMapping("movie")
public class MovieController {
  @RequestMapping("movies")
  public String getMovies() {
    return "movies";
  }
}
```

2. 重启 movie-provider 访问 `localhost:37001/movie/movies` ， 页面显示字面量 `moives` 。


到此，服务提供者可以对外提供服务了。

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

## 关于RestTemplate

是 spring 提供的一种简单便捷的模板类来进行 Http 操作

## 关于Ribbon

1. 是一个 http、tcp 负载均衡
2. 需要连接到注册中心，下载服务列表到本地，之后才可负载均衡
3. eureka-client 依赖了 ribbon ，所以项目不需要显示引用 ribbon


## ribbon 的使用

添加 eureka-client 依赖 -> 配置文件：注册中心 -> 启动类添加 eureka-client 注解 -> 启动类头顶 @EnableDiscoveryClient、RestTemplate 头顶 @LoadBalance

1. 新建模块 `spring-cloud.s3.movie-consumer-ribbon` 

2. 在 pom 中添加依赖： eureka-client 、 ribbon 、 还要提供 web 服务

```
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
  </dependencies>
```

3. 添加配置文件

```
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:35001/eureka/
  instance:
    instance-id: ${spring.cloud.client.ip-address}:${server.port}/${spring.application.name}
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
server:
  port: 33001
spring:
  application:
    name: movie-consumer-ribbon
```

4. 创建启动类 `...MovieConsumerRibbonApplication.java`

```
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class MovieConsumerRibbonApplication {
  public static void main(String[] args) {
    SpringApplication.run(MovieConsumerRibbonApplication.class);
  }

  @Bean
  @LoadBalanced
  RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
```

5. 创建消费者入口， `..MovieController`

```
@RestController
@RequestMapping("/consumer/ribbon/movie")
public class MovieRibbonController {

  @Autowired
  RestTemplate restTemplate;

  @RequestMapping("movies")
  public String getMovies() {
    // 使用 服务名 代替BASE URL ，后面正常接 资源路径
    String GET_MOVIES = "http://movie-provider/movie/movies";
    return GET_MOVIES + " == " + restTemplate.getForObject(GET_MOVIES, String.class);
  }
}
```

第二个 MovieController 了，注意区分角色，这个是 consumer，上一个是 provider。

5. 访问消费者页面 `http://localhost:33001/consumer/ribbon/movie/movies`

## 因为目前还没有高可用环境，说好的负载均衡要等高可用环境完善后才会露头


# 七、Eureka 、 Provider 的高可用环境搭建； Ribbon 的负载均衡的应用

## 先聊一下 Spring 加载配置（此处没有'文件'两字）的优先级

1. spring boot 加载项目内部配置文件 *路径* 的优先级
- 工程根目录:./config/
- 工程根目录：./
- classpath:/config/
- classpath:/

加载优先级是 从上到下，并且每个配置文件都会加载，用高优先级覆盖低优先级，按互补形式覆盖。

> classpath 就是 maven 项目中的 java 和 resource  目录。

2. spring boot 加载 *外部* 配置文件的优先级
- 优先加载 *jar 外部* 的配置文件

> \-folder  
    |- application.yml  
    |- xxx-application.jar

3. 有限加载名称为 bootstrap 配置文件，其次加载名称为 application 的配置文件。

- bootstrap.yml
- application.yml

4. 优先加载带 [-profile] 的配置文件

先加载带 profile 的
- jar外部的 application-prod.yml
- jar内部的 application-prod.yml

再加载不带 profile 的
- jar外部的 application.yml
- jar内部的 application.yml


## 配置文件接收命令行参数

1. 新增 bootstrap.yml
在每个子模块中加入一个新的配置文件， `bootstrap.yml` ， 该配置文件可以指定使用哪个 `profile` 的配置文件，也可以接收命令行参数

```
spring:
  profiles:
    active: dev # 表示激活 application-dev 配置文件
```



## 程序打包

1. 修改根 pom 文件，增加打包方式

```
...

  <build>
      ...
      
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>

    </plugins>
  </build>

</project>
```


## Eureka 的高可用 - 打jar包方式

高可用就意味着多个实例，单机将使用伪集群——host 相同，port 不同。 用多个命令行启动相同 jar ，利用命令行的优先级，传入不同的 port 即可。

1. 给 `maven-resources-plugin` 插件添加拷贝配置文件的配置

在项目根 pom 中更改 `maven-resources-plugin` 插件，加 `<!-- resource copy-->` 配置：
```
      <plugin>
        <!-- analyze placeholder -->
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-resources-plugin</artifactId>
        <configuration>
          <delimiters>
            <delimiter>$</delimiter>
          </delimiters>
        </configuration>
        <!-- resource copy-->
        <executions>
          <execution>
            <id>copy-resources</id>
            <phase>validate</phase>
            <goals>
              <goal>copy-resources</goal>
            </goals>
            <configuration>
              <resources>
                <resource>
                  <directory>src/main/resources</directory>
                  <filtering>true</filtering>
                  <excludes>
                    <exclude>*.bat</exclude>
                  </excludes>
                </resource>
              </resources>
              <outputDirectory>${project.build.directory}</outputDirectory>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

这样 resource 下的配置文件在都会被拷贝到 target 下。

2. 使用 spring cloud 的 bootstrap.yml 配置文件
- 新建配置文件 application-dev.yml， 这个配置文件使用单点 注册中心
```
server:
  port: 35001

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false # 是否从 eureka 获取信息
    fetchRegistry: false # 是否注册到 eureka
  server:
    eviction-interval-timer-in-ms: 60000 # 设置清理的时间间隔，单位是 ms ，默认值 60000 ms(60s)

spring:
  application:
    name: eurka-server
```
- 新建配置文件 application-test.yml， 这个配置文件使用集群 注册中心
```
server:
  port: 35031

eureka:
  instance:
    hostname: localhost
    instance-id: ${spring.application.name}@${spring.cloud.client.ip-address}:${server.port}
  client:
    registerWithEureka: true # 是否从 eureka 获取信息
    fetchRegistry: true # 是否注册到 eureka
    serviceUrl:
      defaultZone: http://localhost:35001/eureka/,http://localhost:35002/eureka/,http://localhost:35003/eureka/
  server:
    eviction-interval-timer-in-ms: 60000 # 设置清理的时间间隔，单位是 ms ，默认值 60000 ms(60s)

spring:
  application:
    name: eurka-server
```

3. 添加 bootstrap.yml 配置文件， 并激活 `test` 为 test 的配置文件
```
spring:
  profiles:
    active: test # 表示激活 application-test 配置文件
```

4. resource 目录下增加启动脚本，当前是 windows 环境，.bat 文件的内容：
```
java -jar spring-cloud.s1.eureka-server-1.0-SNAPSHOT.jar --spring.profiles.active=test --server.port=35001
java -jar spring-cloud.s1.eureka-server-1.0-SNAPSHOT.jar --spring.profiles.active=test --server.port=35002
java -jar spring-cloud.s1.eureka-server-1.0-SNAPSHOT.jar --spring.profiles.active=test --server.port=35003
```
这里一共给出3个 .bat 文件的内容，一个文件分别取一行，分别执行，实现伪集群。

> Intellij IDEA 环境下，在插件仓库搜索 "cmd" ，安装插件，在IDE中即可直接运行 .bat 文件。

> 当前环境下，注册中心没必要高可用。


## Eureka 的高可用 - Intellij Multi Application

1. 打开程序运行配置 Edit Configuration..
2. 拷贝  在"Spring Boot" 下找到名为 "EurekaServerApplication" 的程序，复制3份， 可以点图标，或者使用默认快捷键 `Ctrl+d`。
3. 对这3个新得到的启动，分别配置 JVM 参数 "Program arguments" 
```
--spring.profiles.active=test --server.port=35001
--spring.profiles.active=test --server.port=35002
--spring.profiles.active=test --server.port=35003
```

现在有两种方式可以构建高可用环境了。另外还规定了本地环境的 `profile` 用途， `dev` 为单点环境， `test` 为高可用环境。 还可以定义 `quasi` 之类的，来满足想要的用途。

4. 启动验证一下

> 大多数开发场景下， eureka 都使用单点方式启动。下面不特殊说明的，都是使用单点启动。

## Provider 的高可用

1. 套路与 Eureka 的高可用相同。

2. 注册中心集群了，声明注册中心的地方就需要更改了。更改每个 EurekaClient 的配置文件：

```
defaultZone: http://localhost:35001/eureka/,http://localhost:35002/eureka/,http://localhost:35003/eureka/
```

3. 配置文件规划
针对 eureka 的单点部署 和 集群部署， provider 也要有两套配置，所以，也用 `profile` 解决吧。例如：调试业务功能的时候用 `-dev` 启动一个点就行； 调试负载策略的时候用 `-test` 启动多个点。

4. 要想能看出负载均衡的效果，provider 还要做些改动，至少每个点的返回信息不同，才可以更直观看出效果，正好每个应用的端口号是唯一的...  

创建 `ServerPortConfiguration` ，来获取应用启动端口
```
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
```

MovieControll 修改：

```
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
```

至此，所有准备都已做完。

> 这时，你 IDEA 里面的执行程序们看起来可能有些乱，作者自己的规律是，执行程序名后接 "-序号" 来标记这是集群工作环境的应用，并且只有这些带序号的应用 才有传入参数，他们的 profile 都是 test ， 端口各异。 默认执行程序，名称里不带 "-序号" ， 也没有命令行参数传入， bootstrap 中指定的 profile 为 `dev` 。

## Ribbon 的负载均衡 - 默认策略

前面的准备工作做好后，直接启动 `MovieConsumerRibbonApplication` ，访问他的资源页面 `http://localhost:33001/movie/movies` ，不停的刷新浏览器，体验 Ribbon 默认负载均衡策略 `RoundRobinRule` 带来的乐趣。


## Ribbon 负载均衡策略

- RoundRobinRule  
轮询策略。Ribbon默认采用的策略。

- RandomRule  
随机策略，从所有可用的provider中随机选择一个。

- RetryRule  
先按照RoundRobinRule策略获取provider，若获取失败，则在指定的时限内重试。默认的时限为500毫秒。

- BestAvailableRule  
选择并发量最小的provider，即连接的消费者数量最少的provider

- AvailabilityFilteringRule  
过滤掉处于断路器跳闸状态的provider，或已经超过连接极限的provider，对剩余provider采用轮询策略。
（1）在默认情况下，这台服务器如果3次连接失败，这台服务器就会被设置为“短路”状态。短路状态将持续30秒，如果再次连接失败，短路的持续时间就会几何级地增加。

注意：可以通过修改配置loadbalancer.<clientName>.connectionFailureCountThreshold来修改连接失败多少次之后被设置为短路状态。默认是3次。

（2）并发数过高的服务器。如果一个服务器的并发连接数过高，配置了AvailabilityFilteringRule规则的客户端也会将其忽略。并发连接数的上线，可以由客户端的<clientName>.<clientConfigNameSpace>.ActiveConnectionsLimit属性进行配置。

- ZoneAvoidanceRule  
复合判断provider所在区域的性能及provider的可用性选择服务器。

- WeightedResponseTimeRule  
“权重响应时间”策略。根据每个provider的平均响应时间计算其权重，响应时间越快权重越大，被选中的机率就越高。在刚启动时采用轮询策略。后面就会根据权重进行选择

## Ribbon 负载均衡策略的修改

## Ribbon 负载均衡策略的范围


# 八、Feign

## 关于 Feign

Feign是一个声明式的伪Http客户端，它使得 Http 客户端编码变得更简单。使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，可使用Feign 注解和 JAX-RS 注解。Feign 支持可插拔的编码器和解码器。Feign 默认集成了 Ribbon，自然就实现了负载均衡的效果。

## 使用 Feign

1. 创建一个新模块 `spring-cloud.s4.movie-consumer-feign`
2. 在 pom 中添加依赖
```
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
  </dependencies>
```
3. 添加配置文件

application.yml
```
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:35001/eureka/
  instance:
    instance-id: ${spring.cloud.client.ip-address}:${server.port}/${spring.application.name}
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
server:
  port: 33002
spring:
  application:
    name: movie-consumer-feign
```


4. 创建启动类 `MovieConsumerFeignApplication`
```
/*
@EnableFeignClients 开启 feign
 */
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
public class MovieConsumerFeignApplication {
  public static void main(String[] args) {
    SpringApplication.run(MovieConsumerFeignApplication.class, args);
  }
}

```

5. 使用 feign ， 创建 `MovieFeignService`

```
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
```

这样就写好了，再写一个 Controller 调用这个 service，并且提供用户访问入口

6. 创建 `MovieFeignController`
```
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
```

7. 起动项目、访问、刷新
```
http://localhost:33002/consumer/feign/movie/movies
```
依然支持负载均衡，完美 ...|.  .|...  

























































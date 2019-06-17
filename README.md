# 分布式锁工具包

### 使用方法
### springboot 项目
#### 1.拉取代码并打包
``` cmd
mvn clean install
```

#### 2.pom引用
``` xml
  <dependency>
    <groupId>com.qingyuan1232</groupId>
    <artifactId>redis-lock-spring-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
```
#### 3.项目中使用
#### 注解式 支持SpEL表达式
``` java
    @PostMapping("/test")
    @SLock(key = {"#user"}, lockFailHandler = "lockFailHandler")
    public Object test(String user) throws InterruptedException {
        Thread.sleep(1000);
        return Thread.currentThread() + "success" + user;
    }

    public Object lockFailHandler(String user) {
        return Thread.currentThread() + "lock fail" + user;
    }
```
#### 编程式
``` java
    @Autowired
    private LockTemplate lockTemplate;

    @PostMapping("/test")
    public Object test(String user) throws InterruptedException {
        try {
            if (lockTemplate.lock(user,1000L,1000L)){
                Thread.sleep(1000);
            }else {
                //加锁失败
            }
        }finally {
            lockTemplate.unLock(user);
        }
        return Thread.currentThread() + "success" + user;
    }
```

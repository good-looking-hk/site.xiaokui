## 微型 Spring Cloud 网上商城演示

一切从简，但保留基本的组织架构
### 为了不过多的依赖第三方，尽量采用Spring技术栈
### Dao层采用JPA，Mysql版本为8.0
### 分布式事务采用ByteTCC

分布式选型使用消息队列，还是ByteTcc，有待研究

预下单流程：
    商品模块先锁库存，订单中心生成订单 -- 分布式事务
    
支付完成后：
    商品模块减库存，订单状态置为已支付，为用户加积分 -- 分布式事务



### 服务发现Eureka，端口指定为9000，可访问 http://localhost:9000 以查看服务的注册情况
### API网关Spring Cloud Gateway，端口指定为8000，对外服务均需要统一经过网关
### 用户服务 随机端口
### 商品模块 随机端口
### 订单模块 随机端口

## 关于Spring Cloud Stream里面的几个概念
在普通的pub-sub关系中，多个consumer在订阅了同一个topic时，这些consumer之间是竞争关系，即topic中的一条消息只会被其中一个consumer消费。
但如果这些consumer不属于同一个服务怎么办，例如下单topic的下游会有库存服务、账户服务等多个服务的消费者同时存在，
这些不同服务的消费者都需要获取到下单topic中的消息，否则就无法触发相应的操作，难道需要给不同服务排个队依次传递消息，那就变成了同步操作了。

在kafka中通过Consumer Group消费者分组来处理上述问题。
一个topic中的每一条消息都会采取多副本的方式分发给所有订阅的Consumer Group，每个Consumer Group中的Consumer之间则竞争消费。
即库存服务和账户服务的消费组属于不同的Consumer Group，两个服务都会得到下单topic的消息，但是同一个服务只会有一个Consumer实例会实际消费。 
Spring Clous Stream也支持了kafka的这一特性，每个Consumer可以通过spring.cloud.stream.bindings..group属性设置自己所属的Consumer Group。 
默认情况下，如果我们没有为Consumer指定消费组的话，Spring Cloud Stream会为其分配一个独立的匿名消费组。
所以如果某topic下的所有consumers都未指定消费组时，当有消息发布后，所有的consumers都会对其进行消费，因为它们各自属于独立的组。
因此，我们建议在使用Spring Cloud Stream时最好都指定Consumer Group，以防止对消息的重复消费，除非该行为是必要的（例如刷新所有consumer的配置等）。


给消费者设置消费组和主题

设置消费组： spring.cloud.stream.bindings.<通道名>.group=<消费组名>
设置主题： spring.cloud.stream.bindings.<通道名>.destination=<主题名>
给生产者指定通道的主题：spring.cloud.stream.bindings.<通道名>.destination=<主题名>
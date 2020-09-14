# gulimall


### 项目架构图
![谷粒商城架构图](doc/谷粒商城-微服务架构图.jpg)
#### 介绍

谷粒商城是一个类似京东的自营商城平台，由业务集群系统+后台管理系统构成。



谷粒商城打通了分布式开发的全栈技能，包含前后分离全栈开发、Restful接口、数据校验、网关、注册发现、配置中心、熔断、限流、降级、

链路追踪、性能监控、压力测试、系统预警、集群部署、持续集成、持续部署。



谷粒商城分为三个部分：分布式基础、分布式高级、高可用集群部署



分布式基础篇：使用SpringBoot+Vue+逆向工程搭建全套后台管理系统，使用前后分离方式，以商品系统为例，手把手教大家进行全栈开发。



分布式高级篇：开发整个商城系统，使用SpringBoot+SpringCloud并配套SpringCloud Alibaba系列，引入全套微服务治理方案。Nacos注册中心/配置中心，Sentinel流量保护系统，Seata分布式事务&RabbitMQ柔性事务方案，

SpringCloud-Gateway网关，Feign远程调用，Sleuth+Zipkin链路追踪系统，Spring Cache缓存，SpringSession跨子域Session同步方案，基于ElasticSearch7全文检索，异步编排与线程池，

压力测试调优，Redisson分布式锁，分布式信号量等。通过高级篇开发，大家将会掌握微服务的全套方案。



高可用集群部署篇：基于kubernetes集群（3节点，1主2从，需要至少26G内存，同学们提前自己升级配置或者专门准备一台电脑），

整合kubesphere可视化界面，搭建全套系统环境。使用集群化部署，包括Redis Cluster集群，

MySQL主从与分库分表(使用ShardingSphere完成)集群，RabbitMQ镜像队列集群，ElasticSearch高可用集群。

基于kubesphere整合Jenkins全可视化CICD，全套Pipeline流水线编写，参数化构建+手动确认模式保证。

通过集群篇，快速掌握k8s+kubesphere，可以帮助企业极大的提升生产力。
### 个人学习的一点代码记录，更多信息请参考同目录下的 settings.gradle 文件

#### 欢迎参观
rootProject.name = 'newxiaokui'
#### 记录个人学习的一些代码
include 'k-study:k-java'
#### 记录一些真实遇见的事情
include 'k-study:k-real'
#### mysql分库分表、读写分离、事务测试代码
include 'k-study:k-mysql'

#### 老博客模块，纯原创，开始于2017年，前后端未完全分离
#### 技术架构整体没问题，业务模块也没啥大问题，思而再三，还是决定不用了，改造适配vue工作量过大
#### 不管是前端适配后端，还是后端适配前端，工作量都挺大，而且难度也是有的，耗时耗力
#### 故而选用一个前端后端基本都整合好的，后期只需要看懂，并略微修改即可使用
include 'k-blog:k-blog-user'
include 'k-blog:k-blog-core'
include 'k-blog:k-blog-app'

#### 新博客模块，2020年11月份重写，主要是优化用户体验
#### 为了界面的美化，采用前后端完全分离开发（主要是管理端，vue的界面和操作体验真是没话说）
#### 又由于后台管理的基础模板较多，这里就不再多做重复的轮子了
#### 不是造不出轮子，而是没必要，请知悉（项目源代码一路看下来没压力，且之前已经造了一个差不多了的）
#### 基础模板借鉴自 https:####github.com/elunez/eladmin，约有70%左右的代码重复量
#### 自己补充了大量注释，包含但不限于流程理解、内部逻辑、代码优化、大体实现，以便于自己深入理解，可自行比较本项目代码与原作者代码
#### 技术架构变化有 安全认证框架shiro -> spring security，数据库操作框架beetlsql -> jpa，
#### 重构一时爽，一直重构火葬场，硬着头皮完成了代码迁移
#### 至于前端水平能看出多少，就要看你是不是行家了
include 'k-new-blog:eladmin-common'
include 'k-new-blog:eladmin-system'
include 'k-new-blog:eladmin-logging'
include 'k-new-blog:eladmin-tool'
include 'k-new-blog:eladmin-generator'

#### 基础公共模块模块
include 'k-common:k-base-service'
include 'k-common:k-swagger-starter'

#### 微服务技术栈迷你商城
include 'k-mall:user-service'
include 'k-mall:product-service'
include 'k-mall:order-service'
include 'k-mall:point-service'
include 'k-mall:api-gateway'
include 'k-mall:eureka-server'
include 'k-mall:common-api'

#### 基于netty的斗地主
include 'k-landlords:k-landlords-client'
include 'k-landlords:k-landlords-server'
include 'k-landlords:k-landlords-common'

#### 微信公众号机器人
include 'k-wx:k-mp-robot'

#### sso oauth2 认证支持
include 'k-cloud:oauth-center'
include 'k-cloud:oauth-center:auth-center'
include 'k-cloud:register-center'
include 'k-cloud:register-center:eureka-server'
include 'k-cloud:api-gateway'

#### elk 相关实战代码
include 'k-elk'

#### mini spring
include 'k-spring'

#### flink 相关实战代码
include 'k-flink'

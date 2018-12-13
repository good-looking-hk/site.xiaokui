## 使用说明

在线演示地址：http://www.xiaokui.site/blog

源代码地址：https://github.com/good-looking-hk/site.xiaokui

如果您有充分的时间，可以通过阅读源代码以更好更高的姿势看待这个项目，反之则可以选择是否愿意听我啰嗦几句。

## 一、有图有真相

![效果图1](https://github.com/good-looking-hk/site.xiaokui/raw/master/img/1.png)

![效果图2](https://github.com/good-looking-hk/site.xiaokui/raw/master/img/2.png)

![效果图3](https://github.com/good-looking-hk/site.xiaokui/raw/master/img/3.png)

![效果图4](https://github.com/good-looking-hk/site.xiaokui/raw/master/img/4.png)

![效果图5](https://github.com/good-looking-hk/site.xiaokui/raw/master/img/5.png)

![效果图6](https://github.com/good-looking-hk/site.xiaokui/raw/master/img/6.png)

![效果图7](https://github.com/good-looking-hk/site.xiaokui/raw/master/img/7.png)

![效果图8](https://github.com/good-looking-hk/site.xiaokui/raw/master/img/8.png)

### 二、技术选型

本项目采用以Spring boot + beetl + beetlsql + shiro作为核心技术栈，辅以必要的工具，如缓存工具ehcache，日志工具logback，其他一些Java工具架包如lombok，swagger，hutool，结合当前流行的前段框架如vue，layui，bootstrap，综合而成，涉及技术面广，**该精不泛**，**该泛不精**。

### 三、项目特点

1. 首先是基于shiro深度定制的权限管理，实现了权限缓存，精确到按钮级别的权限控制。
2. 防止暴力破解密码，单个ip尝试单个账号超出限定次数后，会被锁定特定时间（ip + 账号），在此期间如果重复操作，将会刷新锁定时间，闲置特定时间后可以再次尝试。
3. 在线用户管理（管理员可以踢人，这个功能的存在性有待商榷），支持单账号单用户在线（可以设置强制之前登录或之后登录的账号被挤下线，当然也支持单账号多用户在线，且可以设置账号个数限制，基于shiro + ehcache）。
4. 以用户为中心，一切关于用户的敏感数据，管理人员决不能干涉（特定操作及数据库操作除外）。
5. 精心设计的代码，这个项目经过多次重写、重构、技术选型/迭代，里面包含了我大量对于代码/架构的设计与思考，更多特性只能由读者从源代码中发现了，如果你有什么好的建议或点评，可以联系我，权当交个朋友。
6. 个人觉得使用beetl + beetlsql也是项目的一个特点，这两个国产框架经过我的使用，感觉是比thymeleaf，jsp或jpa，mybatis强多了，本人也对其进行了一些深度定制，个人观点，不喜勿喷。
7. 常量大于配置。什么意思呢？就是我的项目里面，约定每个小模块的根目录都有一个常量类，常量类里面的常量，就是这个模块的配置信息。有了这个，可以很方便地跟踪修改，反正我用起来是很爽的，个人观点，不喜勿喷。
8. 不知道该不该说，因为个人感觉前段也是写得不错的，从哪里可以看出呢？我推荐三个地方，1）我博客的界面，精心设计和打磨的；2）js的封装，请直接查看HK.js，里面封装了很多常用功能，个人感觉这个js的封装是很好的；3）整体项目的js和css设计，虽然我是个后台，但是我也有一颗爱美的心，尽管这确实费了我不少的时间。
9. 参考了很多的开源项目，有从github上面看到的，有从开源中国看到的，个人凭感觉记忆有近10个，但后来我发现他们的某些方面水平还不如自己或某些解决方案自己不喜欢，就删了（可能是自己的错觉）；为了一个小的问题，我可能会参考多个项目，从中选取一种我最喜欢的解决方案并加之改善（虽然，有些人的水平他的代码我还是看得有点迷糊，很厉害的那种）。

### 四、自行编译

自行编译需要注意一下几点

- 项目根目录下给了一个原始的md文件，以及对应生成的html文件，读者可以根据这个自动生成相应的博客。
- 一些静态资源由于是存放在非项目目录下的，且数目庞大，就暂时并未添加了，读者可以像我索取（虽然你也可以从我的博客网站上面直接下载）

### 五、联系交流

茫茫人海，感谢您的耐心阅读，谢谢！

我的QQ：467914950
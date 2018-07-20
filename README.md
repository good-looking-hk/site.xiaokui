## 使用说明

在线演示地址：http://www.xiaokui.site:8080/blog

源代码地址：https://github.com/good-looking-hk/site.xiaokui

如果您有充分的时间，可以通过阅读源代码以更好更高的姿势看待这个项目，反之则可以选择是否愿意听我啰嗦几句。

### 一、技术选型

本项目采用以Spring boot + beetl + beetlsql + shiro作为核心技术栈，辅以必要的工具，如缓存工具ehcache，日志工具logback，其他一些Java工具架包如lombok，swagger，hutool，结合当前流行的前段框架如vue，layui，bootstrap，综合而成，涉及技术面广，该精不泛，该泛不精。

### 二、项目特点

1. 首先是基于shiro深度定制的权限管理，实现了权限缓存，精确到按钮级别的权限控制。
2. 防止暴力破解密码，单个ip尝试单个账号超出限定次数后，会被锁定特定时间（ip + 账号），在此期间如果重复操作，将会刷新锁定时间，闲置特定时间后可以再次尝试。
3. 在线用户管理（管理员可以踢人，这个功能的存在性有待商榷），支持单账号单用户在线（可以设置强制之前登录或之后登录的账号被挤下线，当然也支持单账号多用户在线，且可以设置账号个数限制，基于shiro + ehcache）。
4. 以用户为中心，一切关于用户的敏感数据，管理人员决不能干涉（特定操作及数据库操作除外）。
5. 精心设计的代码，这个项目经过多次重写、重构、技术选型/迭代，里面包含了我大量对于代码/架构的设计与思考，更多特性只能由读者从源代码中发现了，如果你有什么好的建议或点评，可以联系我，权当交个朋友。
6. 个人觉得使用beetl + beetlsql也是项目的一个特点，这两个国产框架经过我的使用，感觉是比thymeleaf，jsp或jpa，mybatis强多了，本人也对其进行了一些深度定制，个人观点，不喜勿喷。
7. 常量大于配置。什么意思呢？就是我的项目里面，约定每个小模块的根目录都有一个常量类，常量类里面的常量，就是这个模块的配置信息。有了这个，可以很方便地跟踪修改，反正我用起来是很爽的，个人观点，不喜勿喷。
8. 不知道该不该说，因为个人感觉前段也是写得不错的，从哪里可以看出呢？我推荐三个地方，1）我博客的界面，精心设计和打磨的；2）js的封装，请直接查看HK.js，里面封装了很多常用功能，个人感觉这个js的封装是很好的；3）整体项目的js和css设计，虽然我是个后台，但是我也有一颗爱美的心，尽管这确实费了我不少的时间。
9. 参考了很多的开源项目，有从github上面看到的，有从开源中国看到的，个人凭感觉记忆有近10个，但后来我发现他们的某些方面水平还不如自己或某些解决方案自己不喜欢，就删了（可能是自己的错觉）；为了一个小的问题，我可能会参考多个项目，从中选取一种我最喜欢的解决方案并加之改善（虽然，有些人的水平他的代码我还是看得有点迷糊，很厉害的那种）。
10. 完善的项目架构分离，比如你当前看到的这个源代码，其实它只包含核心的java源代码、项目依赖以及必要的配置文件及一些手写的文件。这些部分相对来说改动的可能性比较大。而一些相对不会改动的前段框架架包就放在了另外一个目录下，这个可以通过spring的静态资源配置，很简单就实现了。但其实我想说的是这个

```java
// 看代码吧，假设您已经看过我写的某篇博客，但其实它的原始界面是这样的
// 包含必要的头文件
@include("/blog/_header.html"){}
<div id="content">
    <div class="panel panel-default content-body markdown-body">
        <div class="panel-heading content-title">
        	// 上一篇链接
            <span class="pre-blog"><a href='${ctxPath}${user.blog.preBlog!"#"'>上一篇:${user.blog.preBlogTitle!"无"}</a></span>
            // 如果为null，则打印 咦，竟然没有标题
            ${user.blog.title!"咦，竟然没有标题"}
			// 下一篇链接
            <span class="next-blog"><a href='${ctxPath}${user.blog.nextBlog}!"#"'>下一篇:${user.blog.nextBlogTitle!"无"}</a></span>
        </div>
        <div class="panel-body">
        	// 这句才是精华，user.blog.filePath假设为1//Java小技巧/git常用命令
        	// 这句其实实现的作用是把/xiaokui/upload/1/Java小技巧/git常用命令.html文件包含进来并打印
        	// 众所周知，实现这么一个功能，即把非项目路径下的非静态文件包含进来，或者说，项目的templates可以设置多个，即一个不行时再去下一个templates查找，这是需要不少的工作的
        	// 但是有了这个功能就可以实现上传文件与项目分离的需求了，这个功能曾经在thymeleaf困扰着我，有了这个以后，每次打包部署都变得方便了许多
            @printFile("blog:" + user.blog.filePath + ".html");
            <blockquote>
                <small>
                    Created by ${user.name} on ${user.blog.createTime, "yyyy-MM-dd"} and updated on ${user.blog.modifiedTime, "yyyy-MM-dd"}
                </small>
            </blockquote>
        </div>
    </div>
</div>
```

其实现原理就是使用了不同的加载器，Spring默认使用的是Class类型的加载器，默认只会在项目路径下寻找，因此在thymeleaf中实现有点困难，需要debug其源代码，很费劲。此时通过beetl，通过下面几行关键代码就可以实现这个功能了

```java
// 获取Spring Boot 的ClassLoader
ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
if (classLoader == null) {
    classLoader = BeetlConfig.class.getClassLoader();
}
// Spring boot默认的模板路径
ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader(classLoader,
        "templates");

// 自定义的模板加载器，这就是所说的常量大于配置了
FileResourceLoader fileResourceLoader = new FileResourceLoader(UPLOAD_PATH);

CompositeResourceLoader compositeResourceLoader = new CompositeResourceLoader();
// 这就是所说的常量大于配置了
compositeResourceLoader.addResourceLoader(new StartsWithMatcher(BLOG_START_FLAG), fileResourceLoader);
compositeResourceLoader.addResourceLoader(new AllowAllMatcher(), classpathResourceLoader);

beetlGroupUtilConfiguration.setResourceLoader(compositeResourceLoader);

public class PrintFile implements Function {
    @Override
    public Object call(Object[] paras, Context ctx) {
        String path = (String) paras[0];
        Resource resource = ctx.gt.getResourceLoader().getResource(path);
        Reader reader = resource.openReader();
        char[] buff = new char[100];
        int rc;
        try {
            while ((rc = reader.read(buff)) != -1) {
                ctx.byteWriter.write(buff, rc);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
```

### 三、自行编译

自行编译需要注意一下几点

- 项目根目录下给了一个原始的md文件，以及对应生成的html文件，读者可以根据这个自动生成相应的博客。
- 一些静态资源由于是存放在非项目目录下的，且数目庞大，就暂时并未添加了，读者可以像我索取（虽然你也可以从我的博客网站上面直接下载）
- 对于给定的sql，里面会有些历史数据残留（历史原因哈），读者自行删除就行了。

### 四、联系交流

茫茫人海，感谢您的耐心阅读，谢谢！

我的QQ：467914950
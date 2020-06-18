package site.xiaokui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import site.xiaokui.config.spring.TestSpringMVC;

/**
 * 答辩思路：
 * 1.首先简单介绍，这个项目是开始于大二的时候，由于不喜欢市面上主流的博客写作方式，于是决定自己搞一个
 * 到现在，这个项目经过了多次技术迭代、系统升级、代码重写，现在还算是功能比较完整的一个项目
 * 2.代码量是绝对够的，只看跟项目的Java代码，近9000行：目录/home/hk/IdeaWorkSpace/newxiaokui/src/main/java下共有：
 * java文件120个，有效java代码(不含注释和空行)5520行，java注释1950行，空白行1219行
 * 统计耗时：161ms
 * 3.技术都是最前沿的技术，我对他们做了一些深度的定制，具有开发速度快，扩展性高的特点，此外，代码的编写水平也是可以的
 * 4.这个项目也是我找工作的主要项目，公司里面的人看了，也觉得可以
 * 5.总共分4个主模块，分别为博客、用户、角色、菜单，两个辅助模块，分别为安全和缓存
 * 6.博客：博客上传发布、浏览，都是根据我自己的需要进行定制的，因为我也在上面写博客，找工作的时候要给别人看。比如我的博客
 * 发布方式是上传文件、支持上下篇浏览、此外，我还吸收另外微信队友访问量的统计方式，就是一天一人对于每篇最多贡献4个阅读量，我的
 * 也是，此外他是一个博客平台，你也可以注册账号在上面使用
 * 7.用户-角色-权限就是权限系统那套东西了
 *
 * 如果你看到这里，说明是有缘人，在此，请允许我郑重向你推荐几个有水平的类
 * {@link TestSpringMVC}里面包含一些对于Spring MVC的理解
 * {@link site.xiaokui.user.config.shiro.ShiroConfig}里面包含一些对于Shiro的理解
 *
 * 本项目开始自2017年，下面是对于项目功能的一些说明：
 * 1、Spring Boot、Beetl、BeetlSql、Shiro的使用和配置，这里的东西贯穿全称，很有多东西
 * 如：beetl模板的使用、Beetlsql的封装、精确到按钮级别的权限控制、Spring定时任务
 * 2、博客查看、博客上传、博客修改、用户管理、角色管理、菜单管理
 * 3、Redis、Echache：防止暴力破解、实时访问量、权限缓存、访问量贡献、完善的日志
 * @author HK
 * @date 2017/06/25 20:22
 */
@SpringBootApplication
public class NewXiaokuiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApp.class, args);
    }
}

package site.xiaokui.module.sys.blog.entity;

import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import site.xiaokui.module.sys.blog.util.BlogUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author HK
 * @date 2018-12-26 22:55
 */
@Getter
public class BlogDetailList {

    private int pro, pub, dirCount;

    private List<List<SysBlog>> nonProtectedList;

    private List<SysBlog> protectedList;

    private List<List<SysBlog>> createTimeList;

    private List<Integer> createYears;

    private List<String> dirs;

    private void resolveDirList(List<SysBlog> blogList, String blogSpace) {
        if (blogList != null && blogList.size() != 0) {
            // 自定义比较器
            blogList.sort(new SysBlog.DirComparator());
            String newDir = blogList.get(0).getDir();
            nonProtectedList = new LinkedList<>();
            protectedList = new LinkedList<>();
            dirs = new LinkedList<>();
            dirs.add(newDir);
            List<SysBlog> temp = new LinkedList<>();
            Iterator<SysBlog> it = blogList.iterator();

            // 分解list成多个子list
            while (it.hasNext()) {
                SysBlog blog = it.next();
                if (blog.getStatus() == BlogStatusEnum.PROTECTED.getCode()) {
                    pro++;
                    blog.setBlogPath(BlogUtil.getBlogPath(blog.getDir(), blog.getName(), blogSpace));
                    protectedList.add(blog);
                } else if (blog.getStatus() == BlogStatusEnum.PUBLIC.getCode()) {
                    pub++;
                    blog.setBlogPath(BlogUtil.getBlogPath(blog.getDir(), blog.getName(), blogSpace));
                    // 如果与上一个是属于同目录，则添加进临时list
                    if (blog.getDir().equals(newDir)) {
                        temp.add(blog);
                    } else {
                        // 切换到下一个目录，把临时list加入根list然后清空，重新加入新的blog，重复
                        newDir = blog.getDir();
                        dirs.add(newDir);
                        nonProtectedList.add(temp);
                        dirCount++;
                        temp = new LinkedList<>();
                        temp.add(blog);
                    }
                }
            }
            // 添加最后一个临时list
            nonProtectedList.add(temp);
            dirCount++;
        }
    }

    private void resolveDateList(List<SysBlog> blogList) {
        if (blogList != null && blogList.size() != 0) {
            // 自定义比较器
            blogList.sort(new SysBlog.DateComparator());
            int newYear = DateUtil.year(blogList.get(0).getCreateTime());
            createTimeList = new LinkedList<>();
            createYears = new LinkedList<>();
            createYears.add(newYear);
            List<SysBlog> temp = new LinkedList<>();
            Iterator<SysBlog> it = blogList.iterator();

            // 分解list成多个子list
            while (it.hasNext()) {
                SysBlog blog = it.next();
                int year = DateUtil.year(blog.getCreateTime());
                if (year == newYear) {
                    temp.add(blog);
                } else {
                    newYear = year;
                    createYears.add(newYear);
                    createTimeList.add(temp);
                    temp = new LinkedList<>();
                    temp.add(blog);
                }
            }
            // 添加最后一个临时list
            createTimeList.add(temp);
        }
    }

    public BlogDetailList(List<SysBlog> blogList, String blogSpace) {
        resolveDirList(blogList, blogSpace);
        resolveDateList(blogList);
    }
}

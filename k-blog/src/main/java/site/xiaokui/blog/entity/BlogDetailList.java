package site.xiaokui.blog.entity;

import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import site.xiaokui.blog.util.BlogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author HK
 * @date 2018-12-26 22:55
 */
public class BlogDetailList {

    @Getter
    private List<SysBlog> allBlogList;

    @Getter
    private List<List<SysBlog>> publicList = new ArrayList<>(), protectedList = new LinkedList<>(), privateList = new LinkedList<>();

    private List<SysBlog> uploadTopNList = new ArrayList<>(), modifyTopNList = new ArrayList<>(), recommendTopNList = new ArrayList<>();

    private List<SysBlog> pubTemp = new ArrayList<>(), proTemp = new LinkedList<>(), priTemp = new LinkedList<>();

    private List[] objects = {pubTemp, proTemp, priTemp};

    private int[] count = new int[3];

    private String[] dir = new String[3];

    /**
     * 用于存储目录排序，也是方便在index1界面展示。泛型为String
     */
    private List priDir = new LinkedList<>(), proDir = new LinkedList<>(), pubDir = new ArrayList<>();

    private List[] dirs = {pubDir, proDir, priDir};

    /**
     * 用于index1的目录、时间遍历
     */
    @Getter
    private List<List<SysBlog>> priCreateTimeList = new LinkedList<>(), proCreateTimeList = new LinkedList<>(), pubCreateTimeList = new ArrayList<>();

    @Getter
    private List<Integer> priCreateYears = new LinkedList<>(), proCreateYears = new LinkedList<>(), pubCreateYears = new ArrayList<>();

    private void resolveDirList(List<SysBlog> blogList, String blogSpace) {
        if (blogList != null && blogList.size() != 0) {
            // 自定义比较器
            blogList.sort(new SysBlog.DirComparator());

            Iterator<SysBlog> it = blogList.iterator();
            // 分解list成多个子list
            while (it.hasNext()) {
                SysBlog blog = it.next();
                blog.setBlogPath(BlogUtil.getBlogPath(blog.getDir(), blog.getName(), blogSpace));
                if (blog.getStatus() == BlogStatusEnum.PUBLIC.getCode()) {
                    handlePublic(blog, pubTemp);
                } else if (blog.getStatus() == BlogStatusEnum.PROTECTED.getCode()) {
                    handleProtected(blog, proTemp);
                } else if (blog.getStatus() == BlogStatusEnum.PRIVATE.getCode()) {
                    handlePrivate(blog, priTemp);
                }
            }
            // 添加最后一个临时list
            if (objects[0].size() != 0) {
                publicList.add(objects[0]);
            }
            if (objects[1].size() != 0) {
                protectedList.add(objects[1]);
            }
            if (objects[2].size() != 0) {
                privateList.add(objects[2]);
            }
        }
    }


    private void handlePublic(SysBlog blog, List<SysBlog> temp) {
        handleList(0, 0, 0, publicList, blog);
    }

    private void handleProtected(SysBlog blog, List<SysBlog> temp) {
        handleList(1, 1, 1, protectedList, blog);

    }

    private void handlePrivate(SysBlog blog, List<SysBlog> temp) {
        handleList(2, 2, 2, privateList, blog);
    }

    /**
     * @param countIndex 用于避免Java里面的值引用传递
     * @param dirIndex   同countIndex
     */
    private void handleList(int countIndex, int dirIndex, int tempIndex, List<List<SysBlog>> list, SysBlog blog) {
        count[countIndex]++;
        if (dir[dirIndex] == null) {
            objects[tempIndex].add(blog);
            dir[dirIndex] = blog.getDir();
            dirs[dirIndex].add(dir[dirIndex]);
            return;
        }
        if (dir[dirIndex].equals(blog.getDir())) {
            objects[tempIndex].add(blog);
        } else {
            // 切换到下一个目录，把临时list加入根list然后清空，重新加入新的blog，重复
            dir[dirIndex] = blog.getDir();
            dirs[dirIndex].add(dir[dirIndex]);
            list.add((objects[tempIndex]));
            // temp必须是全局变量，且必须使用new关键字
            objects[tempIndex] = new ArrayList<>();
            objects[tempIndex].add(blog);
        }
    }

    public void resolveDateList(List<List<SysBlog>> lists, List<Integer> createYears, List<List<SysBlog>> createTimeList) {
        if (lists != null) {
            List<SysBlog> blogList = new LinkedList<>();
            for (List<SysBlog> list : lists) {
                blogList.addAll(list);
            }
            // 自定义比较器
            blogList.sort(new SysBlog.DateComparator(true));
            if (blogList.size() != 0 && blogList.get(0) != null) {
                int newYear = DateUtil.year(blogList.get(0).getCreateTime());
                createYears.add(newYear);
                List<SysBlog> temp = new ArrayList<>();
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
                        temp = new ArrayList<>();
                        temp.add(blog);
                    }
                }
                // 添加最后一个临时list
                createTimeList.add(temp);
            }
        }
    }

    public void resolveTopN(List<List<SysBlog>> pubList) {
        if (pubList != null) {
            List<SysBlog> blogList = new LinkedList<>();
            for (List<SysBlog> list : pubList) {
                blogList.addAll(list);
            }
            // 自定义比较器
            blogList.sort(new SysBlog.DateComparator(true));
            this.uploadTopNList.addAll(blogList);
            blogList.sort(new SysBlog.DateComparator(false));
            this.modifyTopNList.addAll(blogList);
            blogList.sort(new SysBlog.RecommendComparator());
            this.recommendTopNList.addAll(blogList);
        }
    }

    public BlogDetailList(List<SysBlog> blogList, String blogSpace) {
        this.allBlogList = blogList;
        resolveDirList(blogList, blogSpace);
        resolveDateList(publicList, pubCreateYears, pubCreateTimeList);
        resolveDateList(protectedList, proCreateYears, proCreateTimeList);
        resolveDateList(privateList, priCreateYears, priCreateTimeList);
        resolveTopN(publicList);
    }

    public int getPub() {
        return count[0];
    }

    public int getPro() {
        return count[1];
    }

    public int getPri() {
        return count[2];
    }

    public List getPriDir() {
        return dirs[2];
    }

    public List getProDir() {
        return dirs[1];
    }

    public List getPubDir() {
        return dirs[0];
    }

    public List getUploadTopN(int n) {
        if (n > this.uploadTopNList.size()) {
            return this.uploadTopNList;
        }
        return this.uploadTopNList.subList(0, n);
    }

    public List getModifyTopN(int n) {
        if (n > this.modifyTopNList.size()) {
            return this.modifyTopNList;
        }
        return this.modifyTopNList.subList(0, n);
    }

    public List getRecommendTopN(int n) {
        if (n > this.recommendTopNList.size()) {
            return this.recommendTopNList;
        }
        return this.recommendTopNList.subList(0, n);
    }
}

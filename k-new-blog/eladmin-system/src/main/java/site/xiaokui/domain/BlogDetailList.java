package site.xiaokui.domain;

import lombok.Getter;
import site.xiaokui.domain.enums.BlogTypeEnum;
import site.xiaokui.util.BlogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author HK
 * @date 2018-12-26 22:55
 */
@SuppressWarnings("unchecked")
public class BlogDetailList {

    @Getter
    private final List<SysBlog> allBlogList;

    @Getter
    private final List<List<SysBlog>> publicList = new ArrayList<>(), protectedList = new LinkedList<>(), privateList = new LinkedList<>();

    private final List<SysBlog> uploadTopNList = new ArrayList<>(), modifyTopNList = new ArrayList<>(), recommendTopNList = new ArrayList<>();

    private final List<SysBlog> pubTemp = new ArrayList<>(), proTemp = new LinkedList<>(), priTemp = new LinkedList<>();

    private final List[] objects = {pubTemp, proTemp, priTemp};

    private final int[] count = new int[3];

    private final String[] dir = new String[3];

    /**
     * 用于存储目录排序，也是方便在index1界面展示。泛型为String
     */
    private final List<String> priDir = new LinkedList<>(), proDir = new LinkedList<>(), pubDir = new ArrayList<>();

    private final List[] dirs = {pubDir, proDir, priDir};

    /**
     * 用于index1的目录、时间遍历
     */
    @Getter
    private final List<List<SysBlog>> priCreateTimeList = new LinkedList<>(), proCreateTimeList = new LinkedList<>(), pubCreateTimeList = new ArrayList<>();

    @Getter
    private final List<Integer> priCreateYears = new LinkedList<>(), proCreateYears = new LinkedList<>(), pubCreateYears = new ArrayList<>();

    private void resolveDirList(List<SysBlog> blogList, String blogSpace) {
        if (blogList != null && blogList.size() != 0) {
            // 自定义比较器
            blogList.sort(new SysBlog.DirComparator());

            Iterator<SysBlog> it = blogList.iterator();
            // 分解list成多个子list
            while (it.hasNext()) {
                SysBlog blog = it.next();
                blog.setBlogPath(BlogUtil.getBlogPath(blog.getDir(), blog.getFileName(), blogSpace));
                if (blog.getCreateDate() != null) {
                    String str = blog.getCreateDate().toString();
                    blog.setBlogDate(str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8));
                }
                if (BlogTypeEnum.PUBLIC.getCode().equals(blog.getBlogType())) {
                    handlePublic(blog);
                } else if (BlogTypeEnum.PROTECTED.getCode().equals(blog.getBlogType())) {
                    handleProtected(blog);
                } else if (BlogTypeEnum.PRIVATE.getCode().equals(blog.getBlogType())) {
                    handlePrivate(blog);
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

    private void handlePublic(SysBlog blog) {
        handleList(0, 0, 0, publicList, blog);
    }

    private void handleProtected(SysBlog blog) {
        handleList(1, 1, 1, protectedList, blog);

    }

    private void handlePrivate(SysBlog blog) {
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
                int newYear = Integer.parseInt(blogList.get(0).getCreateDate().toString().substring(0, 4));
                createYears.add(newYear);
                List<SysBlog> temp = new ArrayList<>();
                Iterator<SysBlog> it = blogList.iterator();

                // 分解list成多个子list
                while (it.hasNext()) {
                    SysBlog blog = it.next();
                    int year = Integer.parseInt(blog.getCreateDate().toString().substring(0, 4));
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

    public List<String> getPriDir() {
        return dirs[2];
    }

    public List<String> getProDir() {
        return dirs[1];
    }

    public List<String> getPubDir() {
        return dirs[0];
    }

    public List<SysBlog> getUploadTopN(int n) {
        if (n > this.uploadTopNList.size()) {
            return this.uploadTopNList;
        }
        return this.uploadTopNList.subList(0, n);
    }

    public List<SysBlog> getModifyTopN(int n) {
        if (n > this.modifyTopNList.size()) {
            return this.modifyTopNList;
        }
        return this.modifyTopNList.subList(0, n);
    }

    public List<SysBlog> getRecommendTopN(int n) {
        if (n > this.recommendTopNList.size()) {
            return this.recommendTopNList;
        }
        return this.recommendTopNList.subList(0, n);
    }
}

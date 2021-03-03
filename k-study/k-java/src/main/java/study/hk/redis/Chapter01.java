//package study.hk.redis;
//
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.ZParams;
//
//import java.util.*;
//
///**
// * @author HK
// * @date 2019-02-13 21:34
// */
//public class Chapter01 {
//
//    private static final int ONE_WEEK_IN_SECONDS = 7 * 86400;
//    private static final int VOTE_SCORE = 432;
//    private static final int ARTICLES_PER_PAGE = 25;
//
//    public static final void main(String[] args) {
//        new Chapter01().run();
//    }
//
//    public void run() {
//        Jedis conn = new Jedis("localhost");
//        // Redis中默认设置了16个数据库，编号为0~15
//        conn.select(15);
//
//        String articleId = postArticle(
//                conn, "username", "A title", "http://www.google.com");
//        System.out.println("We posted a new article with id: " + articleId);
//        System.out.println("Its HASH looks like:");
//        Map<String, String> articleData = conn.hgetAll("article:" + articleId);
//        for (Map.Entry<String, String> entry : articleData.entrySet()) {
//            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
//        }
//
//        System.out.println();
//
//        articleVote(conn, "other_user", "article:" + articleId);
//        String votes = conn.hget("article:" + articleId, "votes");
//        System.out.println("We voted for the article, it now has votes: " + votes);
//        assert Integer.parseInt(votes) > 1;
//
//        System.out.println("The currently highest-scoring articles are:");
//        List<Map<String, String>> articles = getArticles(conn, 1);
//        printArticles(articles);
//        assert articles.size() >= 1;
//
//        addGroups(conn, articleId, new String[]{"new-group"});
//        System.out.println("We added the article to a new group, other articles include:");
//        articles = getGroupArticles(conn, "new-group", 1);
//        printArticles(articles);
//        assert articles.size() >= 1;
//    }
//
//    /**
//     * 用户发布文章
//     * @param conn redis连接
//     * @param user 用户
//     * @param title 文章标题
//     * @param link 文章链接
//     * @return 文章id
//     */
//    public String postArticle(Jedis conn, String user, String title, String link) {
//        // 初始化文章id
//        String articleId = String.valueOf(conn.incr("article:"));
//        //投票-文章
//        String voted = "voted:" + articleId;
//        // 投票列表添加用户
//        conn.sadd(voted, user);
//        // 设置投票列表的过期时间
//        conn.expire(voted, ONE_WEEK_IN_SECONDS);
//
//        long now = System.currentTimeMillis() / 1000;
//        String article = "article:" + articleId;
//        HashMap<String, String> articleData = new HashMap<>();
//        articleData.put("title", title);
//        articleData.put("link", link);
//        articleData.put("user", user);
//        articleData.put("now", String.valueOf(now));
//        articleData.put("votes", "1");
//        // 将文章信息存入散列
//        conn.hmset(article, articleData);
//        // 将文章信息添加进score有序集合
//        conn.zadd("score:", now + VOTE_SCORE, article);
//        // 将文章信息添加进time有序集合
//        conn.zadd("time:", now, article);
//        return articleId;
//    }
//
//    /**
//     * 文章投票
//     * @param conn redis连接
//     * @param user 用户
//     * @param article 文章
//     */
//    public void articleVote(Jedis conn, String user, String article) {
//        long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
//        // 是否已过截止时间
//        if (conn.zscore("time:", article) < cutoff) {
//            return;
//        }
//
//        String articleId = article.substring(article.indexOf(':') + 1);
//        // 如果投票-文章列表添加记录成功，则更新score有序集合和文章散列
//        if (conn.sadd("voted:" + articleId, user) == 1) {
//            // 投票评分增加
//            conn.zincrby("score:", VOTE_SCORE, article);
//            // 投票数增加
//            conn.hincrBy(article, "votes", 1);
//        }
//    }
//
//    /**
//     * 获取文章
//     */
//    public List<Map<String, String>> getArticles(Jedis conn, int page) {
//        return getArticles(conn, page, "score:");
//    }
//
//    /**
//     * 获取文章
//     * @param conn redis连接
//     * @param page 页数
//     * @param order 有序集合键名
//     * @return 文章列表
//     */
//    public List<Map<String, String>> getArticles(Jedis conn, int page, String order) {
//        int start = (page - 1) * ARTICLES_PER_PAGE;
//        int end = start + ARTICLES_PER_PAGE - 1;
//
//        // 从大到小排序
//        Set<String> ids = conn.zrevrange(order, start, end);
//        List<Map<String, String>> articles = new ArrayList<>();
//        for (String id : ids) {
//            // 获取散列所有信息
//            Map<String, String> articleData = conn.hgetAll(id);
//            articleData.put("id", id);
//            articles.add(articleData);
//        }
//        return articles;
//    }
//
//    /**
//     * 添加分组
//     * @param conn redis连接
//     * @param articleId 文章id
//     * @param toAdd 所属分组
//     */
//    public void addGroups(Jedis conn, String articleId, String[] toAdd) {
//        String article = "article:" + articleId;
//        for (String group : toAdd) {
//            conn.sadd("group:" + group, article);
//        }
//    }
//
//    /**
//     * 获取分组文章
//     */
//    public List<Map<String, String>> getGroupArticles(Jedis conn, String group, int page) {
//        return getGroupArticles(conn, group, page, "score:");
//    }
//
//    /**
//     * 获取分组文章
//     * @param conn redis连接
//     * @param group 分组
//     * @param page 页数
//     * @param order key
//     * @return 分组文章
//     */
//    public List<Map<String, String>> getGroupArticles(Jedis conn, String group, int page, String order) {
//        String key = order + group;
//        if (!conn.exists(key)) {
//            ZParams params = new ZParams().aggregate(ZParams.Aggregate.MAX);
//            conn.zinterstore(key, params, "group:" + group, order);
//            conn.expire(key, 60);
//        }
//        return getArticles(conn, page, key);
//    }
//
//    /**
//     * 打印所有文章
//     */
//    private void printArticles(List<Map<String, String>> articles) {
//        for (Map<String, String> article : articles) {
//            System.out.println("  id: " + article.get("id"));
//            for (Map.Entry<String, String> entry : article.entrySet()) {
//                if (entry.getKey().equals("id")) {
//                    continue;
//                }
//                System.out.println("    " + entry.getKey() + ": " + entry.getValue());
//            }
//        }
//    }
//}

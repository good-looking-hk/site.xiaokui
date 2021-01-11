package second;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * \\ 反斜杠
 * \t 间隔 ('\u0009')
 * \n 换行 ('\u000A')
 * \r 回车 ('\u000D')
 * \d 数字 等价于 [0-9]
 * \D 非数字 等价于 [^0-9]
 * \s 空白符号 [\t\n\x0B\f\r]
 * \S 非空白符号 [^\t\n\x0B\f\r]
 * \w 单独字符 [a-zA-Z_0-9]
 * \W 非单独字符 [^a-zA-Z_0-9]
 * \f 换页符
 * \e Escape
 * \b 一个单词的边界
 * \B 一个非单词的边界
 * \G 前一个匹配的结束
 *
 * split -l 20000 -d -a 2 UserBehavior.csv
 * 获取 实时访问 热门商品
 * @author HK
 * @date 2021-01-06 17:02
 */
public class HotItems {

    static class UserBehavior {
        public long userId;         // 用户ID
        public long itemId;         // 商品ID
        public int categoryId;      // 商品类目ID
        public String behavior;     // 用户行为, 包括("pv", "buy", "cart", "fav") 点击、加购、购买、收藏
        public long timestamp;      // 行为发生的时间戳，单位秒

        /**
         * csv中样例数据依次为 用户ID，商品ID，商品类别，用户行为，发生时间戳
         */
        public UserBehavior(String userId, String itemId, String categoryId, String behavior, String timestamp) {
            this.userId = Long.parseLong(userId);
            this.itemId = Long.parseLong(itemId);
            this.categoryId = Integer.parseInt(categoryId);
            this.behavior = behavior;
            this.timestamp = Long.parseLong(timestamp);
        }
    }

    static class ItemViewCount {
        public long itemId;     // 商品ID
        public long windowStart; // 窗口开始时间戳
        public long windowEnd;  // 窗口结束时间戳
        public long viewCount;  // 商品的点击量

        public ItemViewCount(long itemId, long windowStart, long windowEnd, long viewCount) {
            this.itemId = itemId;
            this.windowStart = windowStart;
            this.windowEnd = windowEnd;
            this.viewCount = viewCount;
        }
    }

    /**
     * COUNT 统计的聚合函数实现，每出现一条记录加一
     * AggregateFunction<IN, ACC, OUT>
     */
    static class CountAgg implements AggregateFunction<UserBehavior, Long, Long> {

        @Override
        public Long createAccumulator() {
            return 0L;
        }

        @Override
        public Long add(UserBehavior value, Long acc) {
            return acc + 1;
        }

        @Override
        public Long getResult(Long acc) {
            return acc;
        }

        @Override
        public Long merge(Long a, Long b) {
            return a + b;
        }
    }

    /**
     * 聚合函数，统计每个产品的平均浏览量浏览量
     * AggregateFunction<IN, ACC, OUT>
     */
    class AverageAgg implements AggregateFunction<UserBehavior, Tuple2<Long, Integer>, Long> {

        @Override
        public Tuple2<Long, Integer> createAccumulator() {
            return new Tuple2<>(0L, 0);
        }

        @Override
        public Tuple2<Long, Integer> add(UserBehavior userBehavior, Tuple2<Long, Integer> tuple2) {
            return new Tuple2<>(tuple2.f0 + userBehavior.timestamp, tuple2.f1 + 1);
        }

        @Override
        public Long getResult(Tuple2<Long, Integer> tuple2) {
            return tuple2.f0 / tuple2.f1;
        }

        @Override
        public Tuple2<Long, Integer> merge(Tuple2<Long, Integer> acc, Tuple2<Long, Integer> acc1) {
            return new Tuple2<>(acc.f0 + acc1.f0, acc.f1 + acc.f1);
        }
    }

    /**
     * 窗口函数，用于输出窗口的结果
     * WindowFunction<IN, OUT, KEY, W extends Window>
     */
    static class WindowResult implements WindowFunction<Long, ItemViewCount, Long, TimeWindow> {

        @Override
        public void apply(Long aLong, TimeWindow window, Iterable<Long> input, Collector<ItemViewCount> out) throws Exception {
            out.collect(new ItemViewCount(aLong, window.getStart(), window.getEnd(), input.iterator().next()));
        }
    }

    /**
     * 求某个窗口中前 N 名的热门点击商品，key 为窗口时间戳，输出为 TopN 的结果字符串
     */
    static class TopNHotItems extends KeyedProcessFunction<Long, ItemViewCount, String> {
        int topSize;

        public TopNHotItems(int topSize) {
            this.topSize = topSize;
        }

        // 用于存储商品与点击数的状态，待收齐同一个窗口的数据后，再触发 TopN 计算
        private ListState<ItemViewCount> itemState;

        @Override
        public void open(Configuration configuration) throws Exception {
            super.open(configuration);
            itemState = getRuntimeContext().getListState(new ListStateDescriptor<>("item-state", ItemViewCount.class));
        }

        @Override
        public void processElement(ItemViewCount itemViewCount, Context context, Collector<String> collector) throws Exception {
            // 把每条数据存入状态列表
            itemState.add(itemViewCount);
            // 注册 windowEnd+1 的 EventTime Timer, 当触发时，说明收齐了属于windowEnd窗口的所有商品数据
            // 也就是当程序看到 windowE nd + 1 的水位线 watermark 时，触发onTimer回调函数
            context.timerService().registerEventTimeTimer(itemViewCount.windowEnd + 1);
        }

        // 定时器触发时，对所有数据排序，并输出结果
        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
            // 获取收到的所有商品点击量
            List<ItemViewCount> allItems = new ArrayList<>();
            for (ItemViewCount item : itemState.get()) {
                allItems.add(item);
            }
            // 提前清除状态中的数据，释放空间
            itemState.clear();
            // 按照count大小排序，并取前n个
            allItems.sort(new Comparator<ItemViewCount>() {
                @Override
                public int compare(ItemViewCount o1, ItemViewCount o2) {
                    return (int) (o2.viewCount - o1.viewCount);
                }
            });
            allItems = allItems.subList(0, topSize);
            // 将排名结果格式化输出
            StringBuilder sb = new StringBuilder();
            String bgnTime = new SimpleDateFormat("HH:mm:ss.S").format(new Date(allItems.get(0).windowStart));
            String endTime = new SimpleDateFormat("HH:mm:ss.S").format(new Date(allItems.get(0).windowEnd));
            sb.append("时间:").append(new Timestamp(timestamp - 1)).append("\n");
            sb.append("窗口:").append(bgnTime).append("-").append(endTime).append("\n");
            // 输出每一个商品的信息
            for (int i = 0; i < allItems.size(); i++) {
                ItemViewCount currentItem = allItems.get(i);
                sb.append("No").append(i + 1).append(":")
                        .append(" 商品ID=").append(currentItem.itemId)
                        .append(" 浏览量=").append(currentItem.viewCount)
                        .append("\n");
            }
            sb.append("================================");
            out.collect(sb.toString());
        }
    }

    public static void main(String[] args) throws Exception {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        // 读取数据,保留原数据 前两万行
        URL fileUrl = HotItems.class.getClassLoader().getResource("UserBehavior.csv");
        assert fileUrl != null;
        DataStream<UserBehavior> dataStream = env.readTextFile(fileUrl.toString())
                .map((String s) -> {
                    String[] dataArray = s.split(",");
                    return new UserBehavior(dataArray[0].trim(), dataArray[1].trim(), dataArray[2].trim(), dataArray[3].trim(), dataArray[4].trim());
                }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<UserBehavior>() {
                    @Override
                    public long extractAscendingTimestamp(UserBehavior u) {
                        return u.timestamp * 1000L;
                    }
                });
        // 处理数据，先过滤的非pv的用户行为，再安装商品id进行统计，这里的时间窗口大小为1小时，滑动时间为5分钟（还可以使用CountWindow）
        // 聚合函数应用于每个窗口和键，对每个元素调用聚合函数，以递增方式聚合值，并将每个键和窗口的状态保持在一个累加器
        DataStream<String> processedStream = dataStream.filter(u -> "pv".equals(u.behavior))
                .keyBy(u -> u.itemId)
                .timeWindow(Time.hours(1), Time.minutes(5))
                .aggregate(new CountAgg(), new WindowResult())
                .keyBy(u -> u.windowEnd)
                .process(new TopNHotItems(3));
        processedStream.print();
        env.execute("hot items job");
    }
}

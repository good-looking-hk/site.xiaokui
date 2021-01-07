package first;

import lombok.Setter;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 参考来源 https://github.com/apache/flink/blob/master/flink-examples/flink-examples-streaming/src/main/java/org/apache/flink/streaming/examples/socket/SocketWindowWordCount.java
 * <p>
 * 该示例代码 统计特定时间窗口内输入字符的次数，先开命令行，再启动程序即可，已对代码做了输出优化，显而易懂
 *
 * @author HK
 * @date 2021-01-05 16:44
 *
 * <p>
 * Implements a streaming windowed version of the "WordCount" program.
 *
 * <p>This program connects to a server socket and reads strings from the socket. The easiest way to
 * try this out is to open a text server (at port 12345) using the <i>netcat</i> tool via
 *
 * <pre>
 * nc -l 12345 on Linux or nc -l -p 12345 on Windows
 * </pre>
 *
 * <p>and run this example with the hostname and the port as arguments.
 */
public class SocketWindowWordCount {

    public static void main(String[] args) throws Exception {

        // the host and the port to connect to
        final String hostname;
        final int port;
        try {
            final ParameterTool params = ParameterTool.fromArgs(args);
            hostname = params.has("hostname") ? params.get("hostname") : "localhost";
            port = params.has("port") ? params.getInt("port") : 12345;
        } catch (Exception e) {
            System.err.println(
                    "No port specified. Please run 'SocketWindowWordCount "
                            + "--hostname <hostname> --port <port>', where hostname (localhost by default) "
                            + "and port is the address of the text server");
            System.err.println(
                    "To start a simple text server, run 'netcat -l <port>' and "
                            + "type the input text into the command line");
            return;
        }

        // get the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // get input data by connecting to the socket
        DataStream<String> text = env.socketTextStream(hostname, port, "\n");

        // parse the data, group it, window it, and aggregate the counts
        SingleOutputStreamOperator<Object> windowCounts =
                text.flatMap(
                        new FlatMapFunction<String, WordWithCount>() {
                            @Override
                            public void flatMap(
                                    String value, Collector<WordWithCount> out) {
                                String str = new SimpleDateFormat("HH:mm:ss SSS").format(new Date());
                                for (String word : value.split("\\s")) {
                                    System.out.println(str + " 客户端输入" + word);
                                    out.collect(new WordWithCount(word, 1L));
                                }
                            }
                        })
                        .keyBy(value -> value.word)
                        // 固定滑动窗口
                        .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                        .reduce(
                                new ReduceFunction<WordWithCount>() {
                                    @Override
                                    public WordWithCount reduce(WordWithCount a, WordWithCount b) {
                                        return new WordWithCount(a.word, a.count + b.count);
                                    }
                                }, new WindowFunction<WordWithCount, Object, String, TimeWindow>() {
                                    @Override
                                    public void apply(String s, TimeWindow window, Iterable<WordWithCount> input, Collector<Object> out) throws Exception {
                                        out.collect(new WordWithCount(s, input.iterator().next().count, window.getStart(), window.getEnd()));
                                    }
                                });

        // print the results with a single thread, rather than in parallel
        windowCounts.print().setParallelism(1);
        env.execute("Socket Window WordCount");
    }

    // ------------------------------------------------------------------------

    /**
     * Data type for words with count.
     */
    @Setter
    public static class WordWithCount {

        public String word;
        public long count;
        public String bgnTime;
        public String endTime;

        public WordWithCount() {
        }

        public WordWithCount(String word, long count) {
            this.word = word;
            this.count = count;
        }

        public WordWithCount(String word, long count, long bgnTime, long endTime) {
            this.word = word;
            this.count = count;
            this.bgnTime = new SimpleDateFormat("HH:mm:ss SSS").format(new Date(bgnTime));
            this.endTime = new SimpleDateFormat("HH:mm:ss SSS").format(new Date(endTime));
        }

        @Override
        public String toString() {
            if (bgnTime != null && endTime != null) {
                return "字符 " + word + " 在 " + bgnTime + "-" + endTime + " 共出现 " + count + " 次";
            }
            return word + " : " + count;
        }
    }
}

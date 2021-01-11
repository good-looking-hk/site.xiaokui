//package second;
//
//import first.WordCountSql;
//import org.apache.flink.api.java.DataSet;
//import org.apache.flink.api.java.ExecutionEnvironment;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
//import org.apache.flink.table.api.Table;
//import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
//
//import java.net.URL;
//
//import static org.apache.flink.table.api.Expressions.$;
//
///**
// * @author HK
// * @date 2021-01-08 15:51
// */
//public class HotItemsTable {
//
//    public static void main(String[] args) {
//        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//        URL fileUrl = HotItems.class.getClassLoader().getResource("UserBehavior.csv");
//
//
//        assert fileUrl != null;
//        DataStream<HotItems.UserBehavior> dataStream = env.readTextFile(fileUrl.toString())
//                .map((String s) -> {
//                    String[] dataArray = s.split(",");
//                    return new HotItems.UserBehavior(dataArray[0].trim(), dataArray[1].trim(), dataArray[2].trim(), dataArray[3].trim(), dataArray[4].trim());
//                }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<HotItems.UserBehavior>() {
//                    @Override
//                    public long extractAscendingTimestamp(HotItems.UserBehavior u) {
//                        return u.timestamp * 1000L;
//                    }
//                });
//
//
//        BatchTableEnvironment tEnv = BatchTableEnvironment.create(env);
//        tEnv.executeSql()
//
//
//        DataSet<WordCountSql.WC> input =
//                env.fromElements(new WordCountSql.WC("Hello", 1), new WordCountSql.WC("Ciao", 1), new WordCountSql.WC("Hello", 1));
//
//        // register the DataSet as a view "WordCount"
//        tEnv.createTemporaryView("WordCount", input, $("word"), $("frequency"));
//
//        // run a SQL query on the Table and retrieve the result as a new Table
//        Table table =
//                tEnv.sqlQuery(
//                        "SELECT word, SUM(frequency) as frequency FROM WordCount GROUP BY word");
//
//        DataSet<WordCountSql.WC> result = tEnv.toDataSet(table, WordCountSql.WC.class);
//
//        result.print();
//    }
//}

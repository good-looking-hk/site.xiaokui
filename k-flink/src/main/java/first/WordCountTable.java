package first;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import static org.apache.flink.table.api.Expressions.$;

/**
 * @author HK
 * @date 2021-01-07 17:07
 */
public class WordCountTable {

    // *************************************************************************
    //     PROGRAM
    // *************************************************************************

    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tEnv = BatchTableEnvironment.create(env);

        DataSet<WC> input =
                env.fromElements(new WC("Hello", 1), new WC("Ciao", 1), new WC("Hello", 1));

        Table table = tEnv.fromDataSet(input);

        Table filtered =
                table.groupBy($("word"))
                        .select($("word"), $("frequency").sum().as("frequency"))
                        .filter($("frequency").isEqual(2));

        DataSet<WC> result = tEnv.toDataSet(filtered, WC.class);

        result.print();
    }

    // *************************************************************************
    //     USER DATA TYPES
    // *************************************************************************

    /** Simple POJO containing a word and its respective count. */
    public static class WC {
        public String word;
        public long frequency;

        // public constructor to make it a Flink POJO
        public WC() {}

        public WC(String word, long frequency) {
            this.word = word;
            this.frequency = frequency;
        }

        @Override
        public String toString() {
            return "WC " + word + " " + frequency;
        }
    }
}

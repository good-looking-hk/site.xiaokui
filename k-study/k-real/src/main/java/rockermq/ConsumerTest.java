//package rockermq;
//
//import org.apache.rocketmq.client.exception.MQBrokerException;
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
//import org.apache.rocketmq.remoting.exception.RemotingException;
//
///**
// * 测试消费问题，包含：单次消费、广播消费、重复消费
// *
// * @author HK
// * @date 2021-02-22 17:09
// */
//public class ConsumerTest extends BaseTest {
//
//    /**
//     * 第一个问题：开1个生产者，3个消费者，确保消息只被一个消费者消费 | 被所有消费者消费一次
//     * <p>
//     * 消费有两种模式，分别是广播模式和集群模式，默认集群模式
//     * 广播模式：一个分组下的每个消费者都会消费完整的 Topic 消息。
//     * 集群模式：一个分组下的消费者瓜分消费 Topic 消息。
//     * 一般我们用的都是集群模式。
//     * <p>
//     * 结论：在单机上，通过设置同一个分组下面不同的instanceName，可以实现消费者的集群消费、广播消费
//     *
//     * 第二个问题：对于广播消费，假设一个消息已经被当前存在的消费者消费，那么后面再上线的消费者还会消费吗
//     *
//     *
//     */
//    public static void testSingleConsumer() throws Exception {
//        executor.execute(() -> {
//            try {
//                initConsumer("127.0.0.1-1", "ConsumerTest-consume1", "ConsumerTestTopic", MessageModel.BROADCASTING);
//                initConsumer("127.0.0.1-2", "ConsumerTest-consume1", "ConsumerTestTopic", MessageModel.BROADCASTING);
////                initConsumer("127.0.0.1-3", "ConsumerTest-consume1", "ConsumerTestTopic", MessageModel.CLUSTERING);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        Thread.sleep(2000);
//        executor.execute(() -> {
//            try {
//                initProducerSendMsgAndClose("ConsumerTest-producer", "ConsumerTestTopic", "ConsumerTestTag", "hello", 4);
//            } catch (MQClientException | RemotingException | InterruptedException | MQBrokerException e) {
//                e.printStackTrace();
//            }
//        });
//        Thread.sleep(2000);
//        executor.execute(() -> {
//            try {
////                initConsumer("127.0.0.1-1", "ConsumerTest-consume1", "ConsumerTestTopic", MessageModel.CLUSTERING);
////                initConsumer("127.0.0.1-2", "ConsumerTest-consume1", "ConsumerTestTopic", MessageModel.CLUSTERING);
//                initConsumer("127.0.0.1-3", "ConsumerTest-consume1", "ConsumerTestTopic", MessageModel.BROADCASTING);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    public static void main(String[] args) throws Exception {
//        testSingleConsumer();
//    }
//}

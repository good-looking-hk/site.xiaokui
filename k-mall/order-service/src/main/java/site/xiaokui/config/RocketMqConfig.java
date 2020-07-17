package site.xiaokui.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 自定义输出渠道
 * @author hk
 * @date 2020-07-12 16:49
 */
@EnableBinding({RocketMqConfig.OutChannel.class})
public class RocketMqConfig {

    private final static String CHANNEL_OUTPUT = "channel-output";

    public interface OutChannel {
        @Output(RocketMqConfig.CHANNEL_OUTPUT)
        MessageChannel orderTxChannelOutput();
    }
}

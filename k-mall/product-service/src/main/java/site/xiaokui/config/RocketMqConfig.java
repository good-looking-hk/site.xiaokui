package site.xiaokui.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author hk
 * @date 2020-07-12 16:49
 */
@EnableBinding({Source.class, RocketMqConfig.MySink.class})
public class RocketMqConfig {

    public interface MySink {
        @Input(Sink.INPUT)
        SubscribableChannel input();

        @Input("inputDlq")
        SubscribableChannel inputDlq();
    }
}

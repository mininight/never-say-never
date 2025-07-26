package never.say.never.test.cfg;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-30
 */
@Configuration
public class ObjectMapperCfg {

    @Bean
    @Order
    Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            builder.serializerByType(Long.class, ToStringSerializer.instance);
        };
    }
}

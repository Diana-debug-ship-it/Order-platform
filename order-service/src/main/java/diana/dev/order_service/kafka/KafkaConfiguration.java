package diana.dev.order_service.kafka;

import diana.dev.api.kafka.OrderPaidEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Bean
    DefaultKafkaProducerFactory<Long, OrderPaidEvent> orderPaidEventDefaultKafkaProducerFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(producerProperties);
    }


    @Bean
    KafkaTemplate<Long, OrderPaidEvent> orderPaidEventKafkaTemplate(
            DefaultKafkaProducerFactory<Long, OrderPaidEvent> orderPaidEventProducerFactory) {
        return new KafkaTemplate<>(orderPaidEventProducerFactory);
    }

//    @Bean
//    public ConsumerFactory<Long, OrderPaidEvent> orderPaidEventConsumerFactory(KafkaProperties properties) {
//
//        Map<String, Object> props = properties.buildConsumerProperties();
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
//        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "diana.dev.api.kafka");
//        return new DefaultKafkaConsumerFactory<>(props);
//
//    }
//
//
//    @Bean
//    public KafkaListenerContainerFactory<?> orderPaidEventListenerFactory(
//            ConsumerFactory<Long, OrderPaidEvent> consumerFactory
//    ) {
//        ConcurrentKafkaListenerContainerFactory<Long, OrderPaidEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory);
//        factory.setBatchListener(false);
//        return factory;
//    }

}

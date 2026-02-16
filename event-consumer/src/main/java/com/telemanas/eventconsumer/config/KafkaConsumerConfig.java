package com.telemanas.eventconsumer.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telemanas.eventconsumer.model.UserSessionEvent;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, UserSessionEvent> userSessionConsumerFactory(
            KafkaProperties kafkaProperties,
            ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());

        // Avoid JsonDeserializer "setter vs config" conflicts by clearing Spring JSON props
        // for this specialized consumer.
        props.remove(JsonDeserializer.VALUE_DEFAULT_TYPE);
        props.remove(JsonDeserializer.TRUSTED_PACKAGES);
        props.remove(JsonDeserializer.TYPE_MAPPINGS);

        JsonDeserializer<UserSessionEvent> valueDeserializer =
                new JsonDeserializer<>(UserSessionEvent.class, objectMapper, false);
        valueDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserSessionEvent>
            userSessionKafkaListenerContainerFactory(
                    ConsumerFactory<String, UserSessionEvent> userSessionConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, UserSessionEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userSessionConsumerFactory);
        return factory;
    }
}

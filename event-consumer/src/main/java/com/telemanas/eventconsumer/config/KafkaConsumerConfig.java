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
import com.telemanas.eventconsumer.model.AgentActivityInput;
import com.telemanas.eventconsumer.model.AutoCallInput;
import com.telemanas.eventconsumer.model.UserSessionInput; // Added import

@Configuration
public class KafkaConsumerConfig {

    // 1. User Session Configuration
    @Bean
    public ConsumerFactory<String, UserSessionInput> userSessionConsumerFactory(
            KafkaProperties kafkaProperties,
            ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.remove(JsonDeserializer.VALUE_DEFAULT_TYPE);
        props.remove(JsonDeserializer.TRUSTED_PACKAGES);
        props.remove(JsonDeserializer.TYPE_MAPPINGS);

        JsonDeserializer<UserSessionInput> valueDeserializer =
                new JsonDeserializer<>(UserSessionInput.class, objectMapper, false);
        valueDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserSessionInput> userSessionKafkaListenerContainerFactory(
            ConsumerFactory<String, UserSessionInput> userSessionConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, UserSessionInput> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userSessionConsumerFactory);
        return factory;
    }

    // 2. AutoCall Configuration
   
    @Bean
    public ConsumerFactory<String, AutoCallInput> autoCallConsumerFactory(
            KafkaProperties kafkaProperties,
            ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.remove(JsonDeserializer.VALUE_DEFAULT_TYPE);
        props.remove(JsonDeserializer.TRUSTED_PACKAGES);
        props.remove(JsonDeserializer.TYPE_MAPPINGS);

        JsonDeserializer<AutoCallInput> valueDeserializer =
                new JsonDeserializer<>(AutoCallInput.class, objectMapper, false);
        valueDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AutoCallInput> autoCallKafkaListenerContainerFactory(
            ConsumerFactory<String, AutoCallInput> autoCallConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, AutoCallInput> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(autoCallConsumerFactory);
        return factory;
    }

    // 3. Agent Activity Configuration
    @Bean
    public ConsumerFactory<String, AgentActivityInput> agentActivityConsumerFactory(
            KafkaProperties kafkaProperties,
            ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.remove(JsonDeserializer.VALUE_DEFAULT_TYPE);
        props.remove(JsonDeserializer.TRUSTED_PACKAGES);
        props.remove(JsonDeserializer.TYPE_MAPPINGS);

        JsonDeserializer<AgentActivityInput> valueDeserializer =
                new JsonDeserializer<>(AgentActivityInput.class, objectMapper, false);
        valueDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AgentActivityInput> agentActivityKafkaListenerContainerFactory(
            ConsumerFactory<String, AgentActivityInput> agentActivityConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, AgentActivityInput> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(agentActivityConsumerFactory);
        return factory;
    }
}
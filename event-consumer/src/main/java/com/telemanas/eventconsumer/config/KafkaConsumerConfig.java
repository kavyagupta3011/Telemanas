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
import com.telemanas.eventconsumer.model.CallRecordInput;
import com.telemanas.eventconsumer.model.CmCdrInput;
import com.telemanas.eventconsumer.model.UserDispositionInput;
import com.telemanas.eventconsumer.model.UserSessionInput; // Added import

@Configuration
public class KafkaConsumerConfig {

    // USER SESSION CONSUMER AND LISTENER 
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



    //  AUTOCALL
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


    // AGENT ACITIVITY 
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



    // USER DISPOSITION 
     @Bean
        public ConsumerFactory<String, UserDispositionInput> userDispositionConsumerFactory(
                KafkaProperties kafkaProperties,
                ObjectMapper objectMapper) {

        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.remove(JsonDeserializer.VALUE_DEFAULT_TYPE);
        props.remove(JsonDeserializer.TRUSTED_PACKAGES);
        props.remove(JsonDeserializer.TYPE_MAPPINGS);

        JsonDeserializer<UserDispositionInput> valueDeserializer =
                new JsonDeserializer<>(UserDispositionInput.class, objectMapper, false);

        valueDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
        }
        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, UserDispositionInput> userDispositionKafkaListenerContainerFactory(
                ConsumerFactory<String, UserDispositionInput> userDispositionConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, UserDispositionInput> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(userDispositionConsumerFactory);

        return factory;
        }

   


   // Call Record Configuration
    @Bean
    public ConsumerFactory<String, CallRecordInput> callRecordConsumerFactory(
            KafkaProperties kafkaProperties,
            ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.remove(JsonDeserializer.VALUE_DEFAULT_TYPE);
        props.remove(JsonDeserializer.TRUSTED_PACKAGES);
        props.remove(JsonDeserializer.TYPE_MAPPINGS);

        JsonDeserializer<CallRecordInput> valueDeserializer =
                new JsonDeserializer<>(CallRecordInput.class, objectMapper, false);
        valueDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CallRecordInput> callKafkaListenerContainerFactory(
            ConsumerFactory<String, CallRecordInput> callRecordConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, CallRecordInput> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(callRecordConsumerFactory);
        return factory;
    }

// cm cdr 
        @Bean
        public ConsumerFactory<String, CmCdrInput> cmCdrConsumerFactory(
                KafkaProperties kafkaProperties,
                ObjectMapper objectMapper) {

        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());

        JsonDeserializer<CmCdrInput> deserializer =
                new JsonDeserializer<>(CmCdrInput.class, objectMapper, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, CmCdrInput>
        cmCdrKafkaListenerContainerFactory(
                ConsumerFactory<String, CmCdrInput> factory) {

        ConcurrentKafkaListenerContainerFactory<String, CmCdrInput> container =
                new ConcurrentKafkaListenerContainerFactory<>();

        container.setConsumerFactory(factory);
        return container;
        }
   
}
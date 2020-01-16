package com.kafka.learnings.config;

import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaProducer;
import io.opentracing.contrib.kafka.TracingProducerInterceptor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class ProducerConfiguration {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.consumer.group-id}")
    private String groupId;
    @Value("${kafka.consumer.offset-config}")
    private String offsetConfig;

    private final Tracer tracer;
    Logger logger = LoggerFactory.getLogger(ProducerConfiguration.class);

    @Autowired
    @Qualifier("producerProperties")
    private Properties properties;

    @Autowired
    private KafkaProducer<String, String> producer;

    public ProducerConfiguration(Tracer tracer) {
        this.tracer = tracer;
    }

    @Bean(name = "producerProperties")
    public Properties properties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, TracingProducerInterceptor.class.getName());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }

    @Bean
    public KafkaProducer<String, String> producer() {
        return new KafkaProducer<String, String>(properties);
    }

    @Bean
    public TracingKafkaProducer<String, String> tracingKafkaProducer() {
        return new TracingKafkaProducer<String, String>(producer, tracer);
    }

}

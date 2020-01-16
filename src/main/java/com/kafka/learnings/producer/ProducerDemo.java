package com.kafka.learnings.producer;

import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaProducer;
import io.opentracing.util.GlobalTracer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ProducerDemo {

    @Value("${topics.topic-name}")
    private String topicName;
    private final Tracer tracer;
    private final TracingKafkaProducer<String, String> tracingKafkaProducer;
    Logger logger = LoggerFactory.getLogger(ProducerRecord.class);

    public ProducerDemo(TracingKafkaProducer<String, String> tracingKafkaProducer) {
        this.tracingKafkaProducer = tracingKafkaProducer;
        tracer = GlobalTracer.get();
    }

    public void sendRecord(int counter) {

        tracingKafkaProducer.send(record(counter), (metadata, exception) -> {
            if (exception == null) {
                logger.info("Message is produced. \n" +
                        "topic : " + metadata.topic() + "\n" +
                        "offset : " + metadata.offset() + "\n" +
                        "partition : " + metadata.partition());
            }
            else{
                logger.error("Failed to send message.");
            }
        });
    }

    public ProducerRecord<String, String> record(int counter) {
        String value = "Hello World! : " + Integer.toString(counter);
        String key = "id_" + Integer.toString(counter);
        return new ProducerRecord<String, String>(topicName, key, value);
    }

    @PostConstruct
    public void produceRecords() {
        for (int i = 0; i < 10; i++) {
            sendRecord(i+100);
        }
        tracingKafkaProducer.close();
    }
}

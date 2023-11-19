package com.Sinorbis.demo.engine;

import com.Sinorbis.demo.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    @Value("${kafka.consumer.topics}")
    private String TOPIC;
    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    public void sendMessage(Message message) {
        logger.info(String.format("# -> Producing message -> %s", message));
        this.kafkaTemplate.send(TOPIC, message);
    }

}

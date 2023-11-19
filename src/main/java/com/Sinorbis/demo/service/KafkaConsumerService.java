package com.Sinorbis.demo.service;

import com.Sinorbis.demo.models.Message;
import com.Sinorbis.demo.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerService {

    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private MessageRepository messageRepository;

    @KafkaListener(topics = "${kafka.consumer.topics}",groupId = "${kafka.consumer.groupId}")
    public void consumeMessage(Message messageContent) {
        logger.info(String.format("# -> Consumed message -> %s", messageContent));
        // Save the message to the database
        messageRepository.save(messageContent);
    }
}
package com.Sinorbis.demo.service;

import com.Sinorbis.demo.engine.KafkaProducer;
import com.Sinorbis.demo.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

@Component
public class MessageGeneratorScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MessageGeneratorScheduler.class);
    long lowerBound = 100L; //  lower bound
    long upperBound = 999999L; // upper bound
    Random random = new Random();
    @Autowired
    private KafkaProducer producer;

    /**
     * Generates and sends messages at a fixed delay using the scheduled task.
     */
    @Scheduled(fixedDelay = 2000)
    public void generateMessages() {
        long generatedLong = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));

        Message message = new Message();
        message.setId(generatedLong);
        message.setCustomerQuery("new data at " + new Date());
        message.setStatus("Unprocessed");
        logger.info("Sending the message {}", message);
        producer.sendMessage(message);
    }
}

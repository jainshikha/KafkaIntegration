package com.Sinorbis.demo.service;

import com.Sinorbis.demo.engine.KafkaProducer;
import com.Sinorbis.demo.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

@Component
public class MessageGeneratorScheduler {

    @Autowired
    private KafkaProducer producer;
    long lowerBound = 100L; // Define your lower bound
    long upperBound = 999999L; // Define your upper bound

    Random random = new Random();

    @Scheduled(fixedDelay = 2000)
    public void generateMessages() {
        long generatedLong = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));
        Message msg = new Message();
        msg.setId(generatedLong);
        msg.setCustomerQuery("new msg at " + new Date());
        msg.setStatus("Unprocessed");
        producer.sendMessage(msg);
    }
}

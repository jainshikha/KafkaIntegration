package com.Sinorbis.demo.config;

import com.Sinorbis.demo.models.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MessageDeserializer implements Deserializer<Message> {

    private static final Logger logger = LoggerFactory.getLogger(MessageDeserializer.class);

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Message deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, Message.class);
        } catch (IOException exception) {
            logger.error("Error deserializing message from topic '{}'", topic, exception);
            // Return a default or null value, depending on your use case
            return null;
        }
    }

}

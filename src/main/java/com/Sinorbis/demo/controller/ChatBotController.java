package com.Sinorbis.demo.controller;

import com.Sinorbis.demo.engine.KafkaProducer;
import com.Sinorbis.demo.models.Message;
import com.Sinorbis.demo.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/chatBot")
public class ChatBotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatBotController.class);
    private final KafkaProducer producer;
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    ChatBotController(KafkaProducer producer) {
        this.producer = producer;
    }


    /**
     * Retrieves messages by their status.
     *
     * @param status The status of messages to retrieve.
     * @return ResponseEntity containing the list of messages with the specified status.
     */
    @GetMapping(value = "/messages")

    public ResponseEntity<List<Message>> getMessagesByStatus(@RequestParam("status") String status) {
        try {
            logger.info("Executing controller GET_API with request param {}", status);
            Optional<List<Message>> optionalMessages = Optional.ofNullable(messageRepository.findByStatus(status));

            if (optionalMessages.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                List<Message> messages = optionalMessages.get();
                return ResponseEntity.ok(messages);
            }
        } catch (Exception exception) {
            logger.error("Error processing GET_API request", exception);
            throw exception; // GlobalExceptionHandler will handle the exception
        }
    }

    /**
     * Handles the HTTP POST request to reply to messages.
     *
     * @param reply The reply message received as a request parameter.
     */
    @PostMapping("/messages")
    public ResponseEntity<String> replyToMessage(@RequestParam("reply") String reply) {
        try {
            logger.info("Executing Controller POST_API with request param {}", reply);
            Optional<List<Message>> optionalMessages = Optional.ofNullable(messageRepository.findByStatus("Unprocessed"));

            if (optionalMessages.isPresent()) {
                for (Message msg : optionalMessages.get()) {
                    String replyMessage = "Reply to message: " + msg.getId() + " - " + reply;
                    msg.setId(msg.getId());
                    msg.setCustomerQueryResponse(replyMessage);
                    msg.setStatus("Processed");
                    this.producer.sendMessage(msg);
                }
                return ResponseEntity.ok("Messages processed successfully");
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception exception) {
            logger.error("Error processing POST_API request", exception);
            throw exception; //  GlobalExceptionHandler will handle the exception
        }
    }

}

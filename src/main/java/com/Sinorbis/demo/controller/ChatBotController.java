package com.Sinorbis.demo.controller;

import com.Sinorbis.demo.engine.KafkaProducer;
import com.Sinorbis.demo.models.Message;
import com.Sinorbis.demo.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/chatBot")
public class ChatBotController {
    private final KafkaProducer producer;
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    ChatBotController(KafkaProducer producer) {
        this.producer = producer;
    }

    @GetMapping(value = "/messages")
    public ResponseEntity<List<Message>> getMessagesByStatus(@RequestParam("status") String status) {

        Optional<List<Message>> optionalMessages = Optional.ofNullable(messageRepository.findByStatus(status));

        if (optionalMessages.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 if no messages found or null
        } else {
            List<Message> messages = optionalMessages.get();
            return ResponseEntity.ok(messages);
        }

    }

    @PostMapping("/messages")
    public void replyToMessage(@RequestParam("reply") String reply) {
        // Logic to process and reply to the message
        Optional<List<Message>> optionalMessages = Optional.ofNullable(messageRepository.findByStatus("Unprocessed"));

        if (optionalMessages.isPresent()) {
            for (Message msg : optionalMessages.get()) {
                String replyMessage = "Reply to message: " + msg.getId() + " - " + reply;
                msg.setId(msg.getId());
                msg.setCustomerQueryResponse(replyMessage);
                msg.setStatus("Processed");
                this.producer.sendMessage(msg);
            }
            // Additional logic to update message status
            // Update status in the database or wherever it's stored
        }
    }
}

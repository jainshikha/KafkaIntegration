package com.Sinorbis.demo;

import com.Sinorbis.demo.controller.ChatBotController;
import com.Sinorbis.demo.engine.KafkaProducer;
import com.Sinorbis.demo.models.Message;
import com.Sinorbis.demo.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatBotController.class)
class ChatBotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaProducer kafkaProducer;

    @MockBean
    private MessageRepository messageRepository;
    private String getBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        byte[] encodedCredentials = Base64.getEncoder().encode(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedCredentials);
    }

    @Test
    void getMessagesByStatus_shouldReturnMessages_whenMessagesExist() throws Exception {
        // Arrange
        String status = "SomeStatus";
        Message mockMessage = new Message(/* initialize with necessary values */);
        Mockito.when(messageRepository.findByStatus(status)).thenReturn(Collections.singletonList(mockMessage));

        // Act and Assert
        mockMvc.perform(get("/chatBot/messages")
                        .param("status", status)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader("user", "password"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockMessage.getId())); // Adjust this based on your Message class
    }

    @Test
    void getMessagesByStatus_shouldReturnNoContent_whenNoMessagesExist() throws Exception {
        // Arrange
        String status = "NonExistentStatus";
        Mockito.when(messageRepository.findByStatus(status)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(get("/chatBot/messages")
                        .param("status", status)
                        .contentType(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader("user", "password"))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void replyToMessage_shouldSendMessage_whenMessagesExist() throws Exception {
        // Arrange
        String reply = "TestReply";
        Message mockMessage = new Message(/* initialize with necessary values */);
        Mockito.when(messageRepository.findByStatus("Unprocessed")).thenReturn(Collections.singletonList(mockMessage));

        // Act and Assert
        mockMvc.perform(post("/chatBot/messages")
                        .param("reply", reply)
                        .contentType(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader("user", "password"))
                )
                .andExpect(status().isOk());

        // Verify that the producer.sendMessage method was called with the correct message
        Mockito.verify(kafkaProducer).sendMessage(Mockito.eq(mockMessage));
    }
}

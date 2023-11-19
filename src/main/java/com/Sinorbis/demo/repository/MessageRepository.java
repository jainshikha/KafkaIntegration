package com.Sinorbis.demo.repository;

import com.Sinorbis.demo.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByStatus(String status);
}
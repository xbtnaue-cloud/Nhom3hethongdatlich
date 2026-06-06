package com.nhakhoa.repository;

import com.nhakhoa.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByContactIDOrderByCreatedAtAsc(int contactID);
    int countByContactIDAndSenderRole(int contactID, String senderRole);
}
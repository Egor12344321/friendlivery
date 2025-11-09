package com.globallogix.chat.service;


import com.globallogix.chat.entity.Chat;
import com.globallogix.chat.entity.ChatMessage;
import com.globallogix.chat.kafka.events.DeliveryEventDto;
import com.globallogix.chat.repository.ChatMessageRepository;
import com.globallogix.chat.repository.ChatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;


    @Transactional
    public void sendMessage(Long deliveryId, Long senderId, String message){
        Chat chat = chatRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new RuntimeException("Chat for this delivery not found"));
        if (!chat.getSenderId().equals(senderId) && !chat.getCourierId().equals(senderId)) {
            throw new RuntimeException("User " + senderId + " is not participant of this chat");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChat(chat);
        chatMessage.setSenderId(senderId);
        chatMessage.setMessageText(message);
        chatMessage.setMessageType(ChatMessage.MessageType.MESSAGE);

        chatMessageRepository.save(chatMessage);

        simpMessagingTemplate.convertAndSend("/topic/chat/" + deliveryId, chatMessage);

    }


    @Transactional
    public void sendSystemMessage(Long deliveryId, String messageText) {

        try {
            log.info("Sending SYSTEM-MESSAGE for delivery: {}; text: {}", deliveryId, messageText);
            Chat chat = chatRepository.findByDeliveryId(deliveryId)
                    .orElseThrow(() -> new RuntimeException("Chat for this delivery not found"));
            ChatMessage systemMessage = new ChatMessage();
            systemMessage.setChat(chat);
            systemMessage.setMessageText(messageText);
            systemMessage.setMessageType(ChatMessage.MessageType.SYSTEM);

            simpMessagingTemplate.convertAndSend("/topic/chat/" + deliveryId, systemMessage);
            log.info("System message sent to delivery {}: {}", deliveryId, messageText);

        } catch (Exception e) {
            log.error("Error sending system message: {}", e.getMessage());
        }
    }
    public List<ChatMessage> getChatHistory(Long deliveryId) {
        Chat chat = chatRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new RuntimeException("Chat for this delivery not found"));

        return chatMessageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId());
    }


    @Transactional
    public void createChat(Long deliveryId, Long senderId, Long courierId){
        log.info("CHAT-SERVICE: Started creation chat for delivery: {}", deliveryId);
        try{
            Chat chat = new Chat();
            chat.setDeliveryId(deliveryId);
            chat.setSenderId(senderId);
            chat.setCourierId(courierId);

            chatRepository.save(chat);
            log.info("CHAT-SERVICE: Chat created successfully and saved to db");
            sendSystemMessage(deliveryId, "Чат для обсуждения доставки создан");

        } catch (Exception e){
            log.error("Creating chat failed: " + e.getMessage());
        }
    }
}

package com.globallogix.chat.kafka;

import com.globallogix.chat.kafka.events.DeliveryEventDto;
import com.globallogix.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class ChatKafkaListener {
    private final ChatService chatService;

    @KafkaListener(topics = "delivery.assigned")
    public void handleDeliveryAssigned(DeliveryEventDto event) {
        log.debug("CHAT: Handled delivery.assigned event. Starting creation chat for delivery: {}", event.getDeliveryId());
        chatService.createChat(event.getDeliveryId(), event.getSenderId(), event.getCourierId());
    }

    @KafkaListener(topics = "delivery.handover.confirmed")
    public void handleHandoverConfirmed(DeliveryEventDto event) {
        log.debug("CHAT: Handled delivery.handover.confirmed event. Started sending system message");
        chatService.sendSystemMessage(event.getDeliveryId(),
                "Посылка передана курьеру");
    }

    @KafkaListener(topics = "delivery.completed")
    public void handleDeliveryCompleted(DeliveryEventDto event) {
        log.debug("CHAT: Handled delivery.completed event. Started sending system message");
        chatService.sendSystemMessage(event.getDeliveryId(), "Доставка завершена!");
    }
}

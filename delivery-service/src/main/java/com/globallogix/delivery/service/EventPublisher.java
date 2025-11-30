package com.globallogix.delivery.service;

import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.kafka.events.DeliveryEventDto;

public interface EventPublisher {
    public void sendDeliveryCreated(Delivery delivery);
    public void sendHandoverConfirmed(Delivery delivery);
    public void sendDeliveryCompleted(Delivery delivery);
    public void sendDeliveryAssigned(Delivery delivery);
    public void sendDeliveryCancelled(Delivery delivery);

}

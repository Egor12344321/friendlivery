package com.globallogix.delivery.dto.request;

import com.globallogix.delivery.entity.DeliveryStatus;

public record StatusUpdateRequest (
        DeliveryStatus status
) {
}

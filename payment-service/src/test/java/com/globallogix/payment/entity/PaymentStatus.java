package com.globallogix.payment.entity;

public enum PaymentStatus {
    CREATED,
    PROCESSING,     // В обработке
    CAPTURED,       // Оплата завершена
    FAILED,         // Ошибка платежа
    REFUNDED,       // Полный возврат
}

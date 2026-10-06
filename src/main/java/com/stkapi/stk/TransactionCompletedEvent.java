package com.stkapi.stk;

import com.stkapi.stk.NotificationPayloadDTO;
import lombok.Getter;

@Getter
public class TransactionCompletedEvent {

    private final NotificationPayloadDTO payload;

    public TransactionCompletedEvent(NotificationPayloadDTO payload) {
        this.payload = payload;
    }
}

package com.stkapi.stk;

import com.stkapi.stk.NotificationPayloadDTO;

public interface NotificationService {
    void processAndDispatchNotification(NotificationPayloadDTO payload);
}

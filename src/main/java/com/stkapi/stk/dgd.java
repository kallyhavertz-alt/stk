package com.stkapi.stk;

import com.stkapi.stk.NotificationPayloadDTO;
import com.stkapi.stk.TransactionCompletedEvent;
import com.stkapi.stk.NotificationService;
import com.stkapi.stk.MpesaSmsFormatter;
import org.slf.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Async
    @EventListener
    public void handleTransactionCompletedEvent(TransactionCompletedEvent event) {
        processAndDispatchNotification(event.getPayload());
    }

    @Override
    public void processAndDispatchNotification(NotificationPayloadDTO payload) {
        // 1. Generate M-Pesa SMS String
        String smsText = MpesaSmsFormatter.formatSms(payload);
        payload.setFormattedSmsBody(smsText);

        // 2. Generate FCM Push Notification Details
        String pushTitle = "M-PESA Payment Sent";
        String pushBody = String.format("Paid Ksh%,.2f to %s", payload.getAmount(), payload.getRecipientOrMerchant());

        payload.setPushTitle(pushTitle);
        payload.setPushBody(pushBody);

        // 3. System Log Output (simulates broadcast before Firebase SDK activation)
        log.info("=================================================");
        log.info("[NOTIFICATION ENGINE DISPATCH]");
        log.info("Type: {}", payload.getType());
        log.info("Ref Code: {}", payload.getTransactionCode());
        log.info("Push Title: {}", pushTitle);
        log.info("Push Body: {}", pushBody);
        log.info("Raw SMS Text: {}", smsText);
        log.info("=================================================");

        // TODO: FCM SDK integration will hook directly here:
        // firebaseMessagingService.sendNotification(userDeviceToken, payload);
    }
}

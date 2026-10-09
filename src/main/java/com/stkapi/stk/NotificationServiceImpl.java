package com.stkapi.stk;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.stkapi.stk.NotificationPayloadDTO;
import com.stkapi.stk.TransactionCompletedEvent;
import com.stkapi.stk.NotificationService;
import com.stkapi.stk.MpesaSmsFormatter;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = Logger.getLogger(NotificationServiceImpl.class.getName());

    private final FirebaseMessaging firebaseMessaging;

    public NotificationServiceImpl(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    @Async
    @EventListener
    public void handleTransactionCompletedEvent(TransactionCompletedEvent event) {
        if (event != null && event.getPayload() != null) {
            processAndDispatchNotification(event.getPayload());
        } else {
            log.warning("Received null TransactionCompletedEvent or null payload.");
        }
    }

    @Override
    public void processAndDispatchNotification(NotificationPayloadDTO payload) {
        if (payload == null) {
            log.severe("Cannot process null NotificationPayloadDTO.");
            return;
        }

        String smsText = MpesaSmsFormatter.formatSms(payload);
        payload.setFormattedSmsBody(smsText);

        String merchant = payload.getRecipientOrMerchant() != null ? payload.getRecipientOrMerchant() : "Merchant";

        // Exact overlay push formatting (e.g. "MPESA" / "UJ8I99842C confirmed. Ksh60.00 sent to FAMILY BANK.")
        String pushTitle = "MPESA";
        String pushBody = String.format("%s confirmed. Ksh%,.2f sent to %s.",
                payload.getTransactionCode(),
                payload.getAmount() != null ? payload.getAmount() : java.math.BigDecimal.ZERO,
                merchant);

        payload.setPushTitle(pushTitle);
        payload.setPushBody(pushBody);

        log.info("=================================================");
        log.info("[NOTIFICATION ENGINE DISPATCH]");
        log.info("Type: " + payload.getType());
        log.info("Ref Code: " + payload.getTransactionCode());
        log.info("Push Title: " + pushTitle);
        log.info("Push Body: " + pushBody);
        log.info("Raw SMS Text: " + smsText);
        log.info("=================================================");

        Message.Builder messageBuilder = Message.builder()
                .setTopic("mpesa_notifications")
                .setNotification(Notification.builder()
                        .setTitle(pushTitle)
                        .setBody(pushBody)
                        .build())
                .putData("transactionCode", safeString(payload.getTransactionCode()))
                .putData("transactionType", payload.getType() != null ? payload.getType().name() : "PAYBILL")
                .putData("amount", payload.getAmount() != null ? payload.getAmount().toPlainString() : "0.00")
                .putData("newBalance", payload.getNewBalance() != null ? payload.getNewBalance().toPlainString() : "0.00")
                .putData("rawSmsText", safeString(smsText))
                .putData("timestamp", payload.getTransactionDate() != null ? payload.getTransactionDate().toString() : "");

        try {
            Message message = messageBuilder.build();
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent FCM message ID: " + response);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to send FCM push notification: " + e.getMessage(), e);
        }
    }

    private String safeString(String input) {
        return input != null ? input : "";
    }
}
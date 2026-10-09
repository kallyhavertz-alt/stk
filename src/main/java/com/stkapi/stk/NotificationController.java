package com.stkapi.stk;

import com.stkapi.stk.NotificationPayloadDTO;
import com.stkapi.stk.NotificationType;
import com.stkapi.stk.MpesaSmsFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/preview")
    public ResponseEntity<?> previewNotification(
            @RequestParam(defaultValue = "PAYBILL") NotificationType type,
            @RequestParam(defaultValue = "1500.00") BigDecimal amount,
            @RequestParam(defaultValue = "KPLC POWER") String merchant,
            @RequestParam(defaultValue = "888888") String identifier) {

        try {
            NotificationPayloadDTO payload = NotificationPayloadDTO.builder()
                    .transactionCode("RKT98231XQ")
                    .type(type)
                    .recipientOrMerchant(merchant)
                    .identifier(identifier)
                    .amount(amount)
                    .newBalance(new BigDecimal("6500.00"))
                    .transactionDate(LocalDateTime.now())
                    .build();

            // Format SMS body
            String smsBody = MpesaSmsFormatter.formatSms(payload);
            payload.setFormattedSmsBody(smsBody);

            String pushTitle = "MPESA";
            String pushBody = String.format("%s confirmed. Ksh%,.2f sent to %s.",
                    payload.getTransactionCode(), amount, merchant);

            payload.setPushTitle(pushTitle);
            payload.setPushBody(pushBody);

            // Optional: Dispatch real FCM push notification during test
            notificationService.processAndDispatchNotification(payload);

            return ResponseEntity.ok(payload);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating notification preview: " + e.getMessage());
        }
    }
}


/*package com.stkapi.stk;

import com.stkapi.stk.NotificationPayloadDTO;
import com.stkapi.stk.NotificationType;
import com.stkapi.stk.MpesaSmsFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @GetMapping("/preview")
    public ResponseEntity<NotificationPayloadDTO> previewNotification(
            @RequestParam(defaultValue = "PAYBILL") NotificationType type,
            @RequestParam(defaultValue = "1500.00") BigDecimal amount,
            @RequestParam(defaultValue = "KPLC POWER") String merchant) {

        NotificationPayloadDTO payload = NotificationPayloadDTO.builder()
                .transactionCode("RKT98231XQ")
                .type(type)
                .recipientOrMerchant(merchant)
                .identifier("888888")
                .amount(amount)
                .newBalance(new BigDecimal("6500.00"))
                .transactionDate(LocalDateTime.now())
                .build();

        payload.setFormattedSmsBody(MpesaSmsFormatter.formatSms(payload));
        payload.setPushTitle("M-PESA Payment Sent");
        payload.setPushBody(String.format("Paid Ksh%,.2f to %s", amount, merchant));

        return ResponseEntity.ok(payload);
    }
}

 */
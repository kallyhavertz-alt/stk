package com.stkapi.stk;

import com.stkapi.stk.NotificationPayloadDTO;
import com.stkapi.stk.NotificationType;
import com.stkapi.stk.MpesaSmsFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
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
package com.stkapi.stk;

import com.stkapi.stk.NotificationType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPayloadDTO {

    private String transactionCode;
    private NotificationType type;
    private String recipientOrMerchant; // Account/Business Name, Merchant Name, or Recipient Name
    private String identifier;          // Business Number / Till Number / Phone Number
    private BigDecimal amount;
    private BigDecimal newBalance;
    private LocalDateTime transactionDate;

    // Formatted Outputs
    private String formattedSmsBody;
    private String pushTitle;
    private String pushBody;
}

package com.stkapi.stk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationPayloadDTO {

    private String transactionCode;
    private NotificationType type;
    private String recipientOrMerchant;
    private String identifier;
    private BigDecimal amount;
    private BigDecimal newBalance;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime transactionDate;

    // Formatted Outputs
    private String formattedSmsBody;
    private String pushTitle;
    private String pushBody;

    /**
     * Helper factory to build payload safely with null guards for missing recipient/identifier.
     */
    public static NotificationPayloadDTO ofPaybill(
            String transactionCode,
            String businessNumber,
            String recipientName,
            BigDecimal amount,
            BigDecimal newBalance,
            LocalDateTime transactionDate
    ) {
        String merchant = (recipientName != null && !recipientName.isBlank()) ? recipientName : "Paybill " + businessNumber;
        String formattedAmount = String.format("Ksh %,.2f", amount);
        String formattedBal = String.format("Ksh %,.2f", newBalance);

        String sms = String.format("%s Confirmed. %s sent to %s for account %s on %s. New balance is %s.",
                transactionCode, formattedAmount, merchant, businessNumber, transactionDate, formattedBal);

        return NotificationPayloadDTO.builder()
                .transactionCode(transactionCode)
                .type(NotificationType.PAYBILL)
                .recipientOrMerchant(merchant)
                .identifier(businessNumber)
                .amount(amount)
                .newBalance(newBalance)
                .transactionDate(transactionDate)
                .pushTitle("Paybill Payment Successful")
                .pushBody(String.format("Paid %s to %s.", formattedAmount, merchant))
                .formattedSmsBody(sms)
                .build();
    }
}
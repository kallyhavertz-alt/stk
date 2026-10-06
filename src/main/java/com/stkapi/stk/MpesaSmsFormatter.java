package com.stkapi.stk;

import com.stkapi.stk.NotificationPayloadDTO;
import java.time.format.DateTimeFormatter;

public class MpesaSmsFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yy 'at' h:mm a");

    public static String formatSms(NotificationPayloadDTO payload) {
        String formattedDate = payload.getTransactionDate().format(DATE_FORMATTER);
        String formattedAmount = String.format("Ksh%,.2f", payload.getAmount());
        String formattedBalance = String.format("Ksh%,.2f", payload.getNewBalance());

        return switch (payload.getType()) {
            case PAYBILL -> String.format(
                    "%s Confirmed. %s sent to %s for account %s on %s. New M-PESA balance is %s. Transaction cost, Ksh0.00.",
                    payload.getTransactionCode(),
                    formattedAmount,
                    payload.getRecipientOrMerchant(),
                    payload.getIdentifier(),
                    formattedDate,
                    formattedBalance
            );

            case BUY_GOODS -> String.format(
                    "%s Confirmed. %s paid to %s. on %s. New M-PESA balance is %s. Transaction cost, Ksh0.00.",
                    payload.getTransactionCode(),
                    formattedAmount,
                    payload.getRecipientOrMerchant(),
                    formattedDate,
                    formattedBalance
            );

            case POCHI -> String.format(
                    "%s Confirmed. %s sent to Pochi la Biashara %s - %s on %s. New M-PESA balance is %s. Transaction cost, Ksh0.00.",
                    payload.getTransactionCode(),
                    formattedAmount,
                    payload.getIdentifier(),
                    payload.getRecipientOrMerchant(),
                    formattedDate,
                    formattedBalance
            );
        };
    }
}

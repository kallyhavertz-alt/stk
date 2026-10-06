package com.stkapi.stk;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PochiResponseDTO {

    private String transactionCode;
    private String phoneNumber;
    private String recipientName;
    private BigDecimal amount;
    private BigDecimal newBalance;
    private String status;
    private LocalDateTime transactionDate;
}

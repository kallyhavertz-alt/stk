package com.stkapi.stk;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaybillResponseDTO {

    private String transactionCode; // e.g., PQRXY3EGRR
    private String businessNumber;
    private String accountNumber;
    private String recipientName;
    private BigDecimal amount;
    private BigDecimal newBalance;
    private String status;
    private LocalDateTime transactionDate;
}
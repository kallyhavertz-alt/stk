package com.stkapi.stk;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaybillResponseDTO {

    private String transactionCode;
    private String businessNumber;
    private String accountNumber;
    private String recipientName;
    private BigDecimal amount;
    private BigDecimal newBalance;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime transactionDate;
}
package com.stkapi.stk;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyGoodsResponseDTO {

    private String transactionCode;
    private String tillNumber;
    private String merchantName;
    private BigDecimal amount;
    private BigDecimal newBalance;
    private String status;
    private LocalDateTime transactionDate;
}

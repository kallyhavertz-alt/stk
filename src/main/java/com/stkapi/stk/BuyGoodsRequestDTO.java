package com.stkapi.stk;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyGoodsRequestDTO {

    @NotBlank(message = "Till number is required")
    @Pattern(regexp = "^\\d{5,7}$", message = "Till number must be between 5 and 7 digits")
    private String tillNumber;

    @NotBlank(message = "Merchant name is required")
    private String merchantName;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum transaction amount is Ksh 1.00")
    private BigDecimal amount;

    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "^\\d{4}$", message = "PIN must be exactly 4 digits")
    private String pin;
}

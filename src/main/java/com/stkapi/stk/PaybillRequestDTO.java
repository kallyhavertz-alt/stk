package com.stkapi.stk;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaybillRequestDTO {

    @NotBlank(message = "Business number is required")
    @Pattern(regexp = "^\\d{5,10}$", message = "Business number must be 5 to 10 digits")
    private String businessNumber;

    @NotBlank(message = "Account number is required")
    @Size(min = 1, max = 30, message = "Account number must be between 1 and 30 characters")
    private String accountNumber;

    @Size(max = 50, message = "Recipient name cannot exceed 50 characters")
    private String recipientName;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum transaction amount is Ksh 1.00")
    @DecimalMax(value = "300000.00", message = "Maximum transaction limit is Ksh 300,000.00")
    private BigDecimal amount;

    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "^\\d{4}$", message = "PIN must be exactly 4 digits")
    private String pin;
}
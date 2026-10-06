package com.stkapi.stk;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PochiRequestDTO {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(07|01|2547|2541)\\d{8}$", message = "Provide a valid Kenyan phone number (e.g. 0712345678 or 254712345678)")
    private String phoneNumber;

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum transaction amount is Ksh 1.00")
    private BigDecimal amount;

    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "^\\d{4}$", message = "PIN must be exactly 4 digits")
    private String pin;
}
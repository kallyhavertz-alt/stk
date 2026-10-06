package com.stkapi.stk;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber; // e.g., Primary wallet account

    @NotNull
    @Column(name = "pin", nullable = false, length = 4)
    private String pin;

    @NotNull
    @Column(name = "current_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentBalance;

    @NotNull
    @Column(name = "minimum_allowed_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal minimumAllowedBalance; // e.g., Ksh 50.00 threshold so balance never hits 0
}

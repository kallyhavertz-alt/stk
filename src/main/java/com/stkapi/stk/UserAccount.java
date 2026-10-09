package com.stkapi.stk;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "user_accounts",
        indexes = {
                @Index(name = "idx_user_acc_number", columnList = "account_number", unique = true)
        }
)
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
    @Column(name = "account_number", nullable = false, unique = true, length = 30)
    private String accountNumber;

    @NotNull
    @Column(name = "pin", nullable = false, length = 100) // Expanded to support hashed PINs (BCrypt)
    private String pin;

    @NotNull
    @Column(name = "current_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentBalance;

    @NotNull
    @Column(name = "minimum_allowed_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal minimumAllowedBalance;

    @Version
    @Column(name = "version")
    private Long version; // Prevents concurrent balance race conditions / double spending

    // Domain Helper Methods

    /**
     * Checks whether the user has sufficient available balance considering minimum balance limits.
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        BigDecimal availableBalance = currentBalance.subtract(minimumAllowedBalance);
        return availableBalance.compareTo(amount) >= 0;
    }

    /**
     * Safely deducts amount from the account. Throws IllegalArgumentException if balance is insufficient.
     */
    public BigDecimal debit(BigDecimal amount) {
        if (!hasSufficientBalance(amount)) {
            throw new IllegalArgumentException("Insufficient funds. Minimum account balance threshold reached.");
        }
        this.currentBalance = this.currentBalance.subtract(amount);
        return this.currentBalance;
    }

    /**
     * Safely credits amount to the account.
     */
    public BigDecimal credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid deposit amount.");
        }
        this.currentBalance = this.currentBalance.add(amount);
        return this.currentBalance;
    }
}
package com.stkapi.stk;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "paybill_transactions",
        indexes = {
                @Index(name = "idx_paybill_ref_code", columnList = "transaction_code", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaybillTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_code", nullable = false, unique = true, length = 10)
    private String transactionCode;

    @Column(name = "business_number", nullable = false, length = 7)
    private String businessNumber;

    @Column(name = "account_number", nullable = false, length = 30)
    private String accountNumber;

    @Column(name = "recipient_name", nullable = false, length = 50)
    private String recipientName;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after_transaction", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfterTransaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @CreationTimestamp
    @Column(name = "transaction_date", updatable = false)
    private LocalDateTime transactionDate;
}
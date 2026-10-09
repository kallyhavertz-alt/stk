package com.stkapi.stk;

import com.stkapi.stk.NotificationPayloadDTO;
import com.stkapi.stk.PaybillRequestDTO;
import com.stkapi.stk.PaybillResponseDTO;
import com.stkapi.stk.NotificationType;
import com.stkapi.stk.PaybillTransaction;
import com.stkapi.stk.TransactionStatus;
import com.stkapi.stk.UserAccount;
import com.stkapi.stk.TransactionCompletedEvent;
import com.stkapi.stk.PaybillTransactionRepository;
import com.stkapi.stk.UserAccountRepository;
import com.stkapi.stk.PaybillService;
import com.stkapi.stk.TransactionCodeGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaybillServiceImpl implements PaybillService {

    private static final String DEFAULT_ACCOUNT_NO = "PRIMARY_WALLET";
    private static final BigDecimal DEFAULT_STARTING_BALANCE = new BigDecimal("8000.00");
    private static final BigDecimal AUTO_RESET_THRESHOLD = new BigDecimal("200.00");

    private final UserAccountRepository accountRepository;
    private final PaybillTransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PaybillServiceImpl(UserAccountRepository accountRepository,
                              PaybillTransactionRepository transactionRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public PaybillResponseDTO processPaybillTransaction(PaybillRequestDTO request) {

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // 1. Fetch or initialize system wallet
        UserAccount account = accountRepository.findByAccountNumber(DEFAULT_ACCOUNT_NO)
                .orElseGet(() -> accountRepository.save(
                        UserAccount.builder()
                                .accountNumber(DEFAULT_ACCOUNT_NO)
                                .pin("1234")
                                .currentBalance(DEFAULT_STARTING_BALANCE)
                                .minimumAllowedBalance(AUTO_RESET_THRESHOLD)
                                .build()
                ));

        // 2. Validate PIN
        if (request.getPin() != null && !request.getPin().isEmpty() && !account.getPin().equals(request.getPin())) {
            throw new IllegalArgumentException("Invalid PIN entered. Default PIN is 1234");
        }

        // 3. Balance Check & Auto-Reset Logic (< 200 resets to 8000)
        BigDecimal projectedBalance = account.getCurrentBalance().subtract(request.getAmount());

        if (projectedBalance.compareTo(AUTO_RESET_THRESHOLD) < 0) {
            account.setCurrentBalance(DEFAULT_STARTING_BALANCE);
            projectedBalance = account.getCurrentBalance().subtract(request.getAmount());
        }

        account.setCurrentBalance(projectedBalance);
        accountRepository.save(account);

        // 4. Resolve recipient merchant name dynamically
        String merchantName = (request.getRecipientName() != null && !request.getRecipientName().isBlank())
                ? request.getRecipientName()
                : "PAYBILL " + request.getBusinessNumber();

        // 5. Generate reference code
        String transactionCode = TransactionCodeGenerator.generateCode(10);

        // 6. Save Paybill Transaction record
        PaybillTransaction transaction = PaybillTransaction.builder()
                .transactionCode(transactionCode)
                .businessNumber(request.getBusinessNumber())
                .accountNumber(request.getAccountNumber())
                .recipientName(merchantName)
                .amount(request.getAmount())
                .balanceAfterTransaction(projectedBalance)
                .status(TransactionStatus.COMPLETED)
                .build();

        transactionRepository.save(transaction);

        LocalDateTime now = LocalDateTime.now();

        // 7. Publish Event for FCM Dispatch
        try {
            NotificationPayloadDTO notificationPayload = NotificationPayloadDTO.builder()
                    .transactionCode(transactionCode)
                    .type(NotificationType.PAYBILL)
                    .recipientOrMerchant(merchantName)
                    .identifier(request.getAccountNumber()) // Pure account number for cleaner SMS output
                    .amount(request.getAmount())
                    .newBalance(projectedBalance)
                    .transactionDate(now)
                    .build();

            eventPublisher.publishEvent(new TransactionCompletedEvent(notificationPayload));
        } catch (Exception e) {
            System.err.println("Failed to publish transaction completed event: " + e.getMessage());
        }

        // 8. Return Response DTO
        return PaybillResponseDTO.builder()
                .transactionCode(transactionCode)
                .businessNumber(request.getBusinessNumber())
                .accountNumber(request.getAccountNumber())
                .recipientName(merchantName)
                .amount(request.getAmount())
                .newBalance(projectedBalance)
                .status("COMPLETED")
                .transactionDate(now)
                .build();
    }
}
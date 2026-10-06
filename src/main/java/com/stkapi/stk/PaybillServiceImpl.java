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
    private final ApplicationEventPublisher eventPublisher; // 1. Field declared at top

    // 2. Injected into constructor
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

        // 1. Fetch or initialize system wallet
        UserAccount account = accountRepository.findByAccountNumber(DEFAULT_ACCOUNT_NO)
                .orElseGet(() -> accountRepository.save(
                        UserAccount.builder()
                                .accountNumber(DEFAULT_ACCOUNT_NO)
                                .pin("1234") // Default test PIN
                                .currentBalance(DEFAULT_STARTING_BALANCE)
                                .minimumAllowedBalance(AUTO_RESET_THRESHOLD)
                                .build()
                ));

        // 2. Validate PIN
        if (!account.getPin().equals(request.getPin())) {
            throw new IllegalArgumentException("Invalid PIN entered");
        }

        // 3. Balance Check & Auto-Reset Logic
        BigDecimal projectedBalance = account.getCurrentBalance().subtract(request.getAmount());

        if (projectedBalance.compareTo(AUTO_RESET_THRESHOLD) < 0) {
            account.setCurrentBalance(DEFAULT_STARTING_BALANCE);
            projectedBalance = account.getCurrentBalance().subtract(request.getAmount());
        }

        account.setCurrentBalance(projectedBalance);
        accountRepository.save(account);

        // 4. Generate unique 10-char reference code
        String transactionCode;
        do {
            transactionCode = TransactionCodeGenerator.generateCode(10);
        } while (transactionRepository.existsByTransactionCode(transactionCode));

        // 5. Save Paybill Transaction record
        PaybillTransaction transaction = PaybillTransaction.builder()
                .transactionCode(transactionCode)
                .businessNumber(request.getBusinessNumber())
                .accountNumber(request.getAccountNumber())
                .recipientName(request.getRecipientName())
                .amount(request.getAmount())
                .balanceAfterTransaction(projectedBalance)
                .status(TransactionStatus.COMPLETED)
                .build();

        transactionRepository.save(transaction);

        LocalDateTime now = LocalDateTime.now();

        // 6. PUBLISH EVENT BEFORE RETURNING RESPONSE (Inside Method Body)
        NotificationPayloadDTO notificationPayload = NotificationPayloadDTO.builder()
                .transactionCode(transactionCode)
                .type(NotificationType.PAYBILL)
                .recipientOrMerchant(request.getRecipientName())
                .identifier(request.getBusinessNumber() + " Acc " + request.getAccountNumber())
                .amount(request.getAmount())
                .newBalance(projectedBalance)
                .transactionDate(now)
                .build();

        eventPublisher.publishEvent(new TransactionCompletedEvent(notificationPayload));

        // 7. Return Response DTO
        return PaybillResponseDTO.builder()
                .transactionCode(transactionCode)
                .businessNumber(request.getBusinessNumber())
                .accountNumber(request.getAccountNumber())
                .recipientName(request.getRecipientName())
                .amount(request.getAmount())
                .newBalance(projectedBalance)
                .status("COMPLETED")
                .transactionDate(now)
                .build();
    }
}
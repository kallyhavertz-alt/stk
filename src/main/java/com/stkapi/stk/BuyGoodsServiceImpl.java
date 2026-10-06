package com.stkapi.stk;

import com.stkapi.stk.BuyGoodsRequestDTO;
import com.stkapi.stk.BuyGoodsResponseDTO;
import com.stkapi.stk.BuyGoodsTransaction;
import com.stkapi.stk.TransactionStatus;
import com.stkapi.stk.UserAccount;
import com.stkapi.stk.BuyGoodsTransactionRepository;
import com.stkapi.stk.UserAccountRepository;
import com.stkapi.stk.BuyGoodsService;
import com.stkapi.stk.TransactionCodeGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BuyGoodsServiceImpl implements BuyGoodsService {

    private static final String DEFAULT_ACCOUNT_NO = "PRIMARY_WALLET";
    private static final BigDecimal DEFAULT_STARTING_BALANCE = new BigDecimal("8000.00");
    private static final BigDecimal AUTO_RESET_THRESHOLD = new BigDecimal("200.00");

    private final UserAccountRepository accountRepository;
    private final BuyGoodsTransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BuyGoodsServiceImpl(UserAccountRepository accountRepository,
                               BuyGoodsTransactionRepository transactionRepository,
                               ApplicationEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public BuyGoodsResponseDTO processBuyGoodsTransaction(BuyGoodsRequestDTO request) {

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
        if (!account.getPin().equals(request.getPin())) {
            throw new IllegalArgumentException("Invalid PIN entered");
        }

        // 3. Balance Check & Auto-Reset to Ksh 8,000 when below Ksh 200
        BigDecimal projectedBalance = account.getCurrentBalance().subtract(request.getAmount());

        if (projectedBalance.compareTo(AUTO_RESET_THRESHOLD) < 0) {
            account.setCurrentBalance(DEFAULT_STARTING_BALANCE);
            projectedBalance = account.getCurrentBalance().subtract(request.getAmount());
        }

        // 4. Save updated balance
        account.setCurrentBalance(projectedBalance);
        accountRepository.save(account);

        // 5. Generate unique 10-character code
        String transactionCode;
        do {
            transactionCode = TransactionCodeGenerator.generateCode(10);
        } while (transactionRepository.existsByTransactionCode(transactionCode));

        // 6. Save Buy Goods transaction record
        BuyGoodsTransaction transaction = BuyGoodsTransaction.builder()
                .transactionCode(transactionCode)
                .tillNumber(request.getTillNumber())
                .merchantName(request.getMerchantName())
                .amount(request.getAmount())
                .balanceAfterTransaction(projectedBalance)
                .status(TransactionStatus.COMPLETED)
                .build();

        transactionRepository.save(transaction);

        LocalDateTime now = LocalDateTime.now();

        // 7. Publish Event to Notification Engine
        NotificationPayloadDTO notificationPayload = NotificationPayloadDTO.builder()
                .transactionCode(transactionCode)
                .type(NotificationType.BUY_GOODS)
                .recipientOrMerchant(request.getMerchantName())
                .identifier(request.getTillNumber())
                .amount(request.getAmount())
                .newBalance(projectedBalance)
                .transactionDate(now)
                .build();

        eventPublisher.publishEvent(new TransactionCompletedEvent(notificationPayload));

        // 8. Return Response DTO
        return BuyGoodsResponseDTO.builder()
                .transactionCode(transactionCode)
                .tillNumber(request.getTillNumber())
                .merchantName(request.getMerchantName())
                .amount(request.getAmount())
                .newBalance(projectedBalance)
                .status("COMPLETED")
                .transactionDate(now)
                .build();
    }
}
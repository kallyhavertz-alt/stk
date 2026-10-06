package com.stkapi.stk;

import com.stkapi.stk.PaybillTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaybillTransactionRepository extends JpaRepository<PaybillTransaction, Long> {

    Optional<PaybillTransaction> findByTransactionCode(String transactionCode);

    boolean existsByTransactionCode(String transactionCode);
}
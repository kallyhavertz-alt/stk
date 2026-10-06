package com.stkapi.stk;

import com.stkapi.stk.PochiTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PochiTransactionRepository extends JpaRepository<PochiTransaction, Long> {

    boolean existsByTransactionCode(String transactionCode);

    Optional<PochiTransaction> findByTransactionCode(String transactionCode);
}

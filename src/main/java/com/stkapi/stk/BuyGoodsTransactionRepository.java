package com.stkapi.stk;

import com.stkapi.stk.BuyGoodsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyGoodsTransactionRepository extends JpaRepository<BuyGoodsTransaction, Long> {

    boolean existsByTransactionCode(String transactionCode);

    Optional<BuyGoodsTransaction> findByTransactionCode(String transactionCode);
}

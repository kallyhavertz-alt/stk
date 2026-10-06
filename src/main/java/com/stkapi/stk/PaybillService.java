package com.stkapi.stk;

import com.stkapi.stk.PaybillRequestDTO;
import com.stkapi.stk.PaybillResponseDTO;

public interface PaybillService {

    /**
     * Verifies PIN, validates balance threshold, deducts funds,
     * generates a 10-char reference code, and records the transaction.
     */
    PaybillResponseDTO processPaybillTransaction(PaybillRequestDTO request);
}
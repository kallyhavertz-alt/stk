package com.stkapi.stk;

import com.stkapi.stk.PochiRequestDTO;
import com.stkapi.stk.PochiResponseDTO;

public interface PochiService {
    PochiResponseDTO processPochiTransaction(PochiRequestDTO request);
}

package com.stkapi.stk;

import com.stkapi.stk.BuyGoodsRequestDTO;
import com.stkapi.stk.BuyGoodsResponseDTO;

public interface BuyGoodsService {
    BuyGoodsResponseDTO processBuyGoodsTransaction(BuyGoodsRequestDTO request);
}

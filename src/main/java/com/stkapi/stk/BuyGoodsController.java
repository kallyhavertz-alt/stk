package com.stkapi.stk;

import com.stkapi.stk.BuyGoodsRequestDTO;
import com.stkapi.stk.BuyGoodsResponseDTO;
import com.stkapi.stk.BuyGoodsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/payments")
public class BuyGoodsController {

    private final BuyGoodsService buyGoodsService;

    public BuyGoodsController(BuyGoodsService buyGoodsService) {
        this.buyGoodsService = buyGoodsService;
    }

    @PostMapping("/buygoods")
    public ResponseEntity<BuyGoodsResponseDTO> processBuyGoods(@Valid @RequestBody BuyGoodsRequestDTO request) {
        BuyGoodsResponseDTO response = buyGoodsService.processBuyGoodsTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

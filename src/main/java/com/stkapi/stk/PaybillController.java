package com.stkapi.stk;

import com.stkapi.stk.PaybillRequestDTO;
import com.stkapi.stk.PaybillResponseDTO;
import com.stkapi.stk.PaybillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments/paybill")
public class PaybillController {

    private final PaybillService paybillService;

    public PaybillController(PaybillService paybillService) {
        this.paybillService = paybillService;
    }

    @PostMapping
    public ResponseEntity<PaybillResponseDTO> processPaybill(@Valid @RequestBody PaybillRequestDTO request) {
        PaybillResponseDTO response = paybillService.processPaybillTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
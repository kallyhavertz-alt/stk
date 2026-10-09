package com.stkapi.stk;

import com.stkapi.stk.PaybillRequestDTO;
import com.stkapi.stk.PaybillResponseDTO;
import com.stkapi.stk.PaybillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/payments/paybill")
public class PaybillController {

    private final PaybillService paybillService;

    public PaybillController(PaybillService paybillService) {
        this.paybillService = paybillService;
    }

    @PostMapping
    public ResponseEntity<?> processPaybill(@Valid @RequestBody PaybillRequestDTO request) {
        System.out.println(">>> [PAYBILL REQUEST RECEIVED]: " + request);
        try {
            PaybillResponseDTO response = paybillService.processPaybillTransaction(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Paybill Business Logic Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Paybill Server Error: " + e.getMessage());
            e.printStackTrace(); // Logs full stack trace to IntelliJ terminal
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
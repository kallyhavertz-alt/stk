package com.stkapi.stk;

import com.stkapi.stk.PochiRequestDTO;
import com.stkapi.stk.PochiResponseDTO;
import com.stkapi.stk.PochiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PochiController {

    private final PochiService pochiService;

    public PochiController(PochiService pochiService) {
        this.pochiService = pochiService;
    }

    @PostMapping("/pochi")
    public ResponseEntity<PochiResponseDTO> processPochi(@Valid @RequestBody PochiRequestDTO request) {
        PochiResponseDTO response = pochiService.processPochiTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

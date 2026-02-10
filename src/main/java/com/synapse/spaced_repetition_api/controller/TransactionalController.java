package com.synapse.spaced_repetition_api.controller;


import com.synapse.spaced_repetition_api.dto.PayDTO;
import com.synapse.spaced_repetition_api.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bank")
public class TransactionalController {
    @Autowired
    private PaymentService service;

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody PayDTO dto) {
        service.payment(dto);
        // 2. Trả về thông báo thành công đơn giản
        return ResponseEntity.ok("Đã đăng ký thành công");
    }
}

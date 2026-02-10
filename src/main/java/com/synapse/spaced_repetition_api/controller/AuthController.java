package com.synapse.spaced_repetition_api.controller;


import com.synapse.spaced_repetition_api.dto.LoginDTO;
import com.synapse.spaced_repetition_api.dto.RegisterDTO;
import com.synapse.spaced_repetition_api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDTO registerBody) {
        // 1. Gọi Service thực hiện lưu trữ
        authService.register(registerBody);

        // 2. Trả về thông báo thành công đơn giản
        return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký thành công!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginDTO) {
        // 1. Gọi AuthService để thực hiện quy trình Manager -> Provider -> Tool
        // Kết quả trả về là một chuỗi JWT (Tấm thẻ thông hành)
        String jwt = authService.login(loginDTO.getUsername(), loginDTO.getPassword());

        // 2. Trình diện kết quả cho khách hàng (Frontend)
        // Trả về HTTP 200 OK kèm theo object chứa Token
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/id")
    public ResponseEntity<?> UID(@RequestBody LoginDTO loginDTO){
        Long id = authService.id(loginDTO.getUsername(),loginDTO.getPassword());

        return ResponseEntity.ok(id);
    }
}

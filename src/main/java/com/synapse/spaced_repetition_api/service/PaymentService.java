package com.synapse.spaced_repetition_api.service;


import com.synapse.spaced_repetition_api.constant.UserRole;
import com.synapse.spaced_repetition_api.dto.PayDTO;
import com.synapse.spaced_repetition_api.entity.User;
import com.synapse.spaced_repetition_api.exception.CardLimitExceededException;
import com.synapse.spaced_repetition_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.smartcardio.CardException;

@Service
@Transactional
public class PaymentService {
    @Autowired
    private UserRepository repository;

    public void payment(PayDTO dto) {
        User user = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // 1. So limit cards của role và của user hiện sở hữu
        // Giả sử ông có logic đếm số thẻ ở đây
        if (user.getRole().getMaxFlashcards() > user.getMaxFlashcards()) {
            throw new CardLimitExceededException("Số thẻ hiện tại đã vượt giới hạn. Hãy đăng ký gói cao cấp để thêm nhiều tiện ích!");
        }

        // 2. Logic phân hạng Role
        UserRole role;
        if (dto.getAmount() >= 1000000) role = UserRole.PREMIUM;
        else if (dto.getAmount() >= 500000) role = UserRole.VIP;
        else throw new RuntimeException("Số dư không đủ để nâng cấp gói!");

        user.setRole(role);
        user.setFlashcards_now(role.getMaxFlashcards());
        repository.save(user);
    }
}

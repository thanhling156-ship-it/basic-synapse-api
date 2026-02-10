package com.synapse.spaced_repetition_api.service;


import com.synapse.spaced_repetition_api.constant.UserRole;
import com.synapse.spaced_repetition_api.dto.PayDTO;
import com.synapse.spaced_repetition_api.entity.User;
import com.synapse.spaced_repetition_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PaymentService {
    @Autowired
    private UserRepository repository;

    public void payment(PayDTO dto){
        Long id = dto.getId();
        double amount = dto.getAmount();
        UserRole role = null;
        int maxFlashcards = 0;

        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User để nạp tiền!"));
        if(amount > 999000){
            role = UserRole.PREMIUM;
        }
        else if (amount > 499000){
            role = UserRole.VIP;
        }
        else {
            throw new RuntimeException("Không đủ số dư");
        }
        user.setRole(role);
        user.setMaxFlashcards(role.getMaxFlashcards());
        repository.save(user);
    }
}

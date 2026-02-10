package com.synapse.spaced_repetition_api.service;


import com.synapse.spaced_repetition_api.constant.UserRole;
import com.synapse.spaced_repetition_api.dto.RegisterDTO;
import com.synapse.spaced_repetition_api.entity.User;
import com.synapse.spaced_repetition_api.repository.UserRepository;
import com.synapse.spaced_repetition_api.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//Dùng để kiểm tra logic khi login lần đầu
@Service
public class AuthService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtUtils utils;

    @Autowired
    private PasswordEncoder encoder;

    public String login(String username, String password){
        try {
            //Form/DTO cho manager để gửi cho hàm logic phù hợp
            UsernamePasswordAuthenticationToken requestToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            //Chỉ để kiểm tra password
            Authentication resultToken = manager.authenticate(requestToken);

            return utils.generateToken(resultToken.getName());
        }
        catch(AuthenticationException e){
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
    }

    public Long id(String username, String password){
        try {
            UsernamePasswordAuthenticationToken requestToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            Authentication resultToken = manager.authenticate(requestToken);

            // Lấy username từ resultToken (đã được confirm là đúng)
            String authenticatedUsername = resultToken.getName();

            // Truy tìm ID trong PostgreSQL
            return repository.findShadowUser(authenticatedUsername)
                    .map(User::getId)
                    .orElseThrow(() -> new RuntimeException("Lỗi hy hữu: Xác thực xong nhưng không thấy User trong DB!"));
        }
        catch(AuthenticationException e){
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
    }

    public void register(RegisterDTO dto){
        if(repository.existsByUsername(dto.getUsername())){
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
        String encodedPassword = encoder.encode(dto.getPassword());

        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setPassword(encodedPassword);
        // Trong file AuthService.java
        newUser.setRole(UserRole.BASIC);// Gán quyền mặc định
        // Cần tạo ra 1 service riêng đặc biệt để xử lý ROLE
        newUser.setEmail(dto.getEmail());
        repository.save(newUser);
    }
}

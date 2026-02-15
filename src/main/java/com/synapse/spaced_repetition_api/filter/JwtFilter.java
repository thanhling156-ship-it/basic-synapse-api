package com.synapse.spaced_repetition_api.filter;



import java.io.IOException;

import com.synapse.spaced_repetition_api.service.AuthService;
import com.synapse.spaced_repetition_api.service.UserDetailsServiceImpl;
import com.synapse.spaced_repetition_api.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

//Đây là nơi đầu tiên request vào
@Component
@Service
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl detailsService;

    //Hàm để kiểm tra vé Token
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("🔍 Request đang vào cửa " + request.getRequestURI());
        try{
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String username = null;

            if(authHeader != null && authHeader.startsWith("Bearer ")){
                token = authHeader.substring(7);

                if(jwtUtils.validateToken(token)){
                    username = jwtUtils.getUsernameFromToken(token);
                }
            }
            //SecurityContextHolder để xác nhận rằng ở trước đã authenticate thì bỏ qua
            //Không bị ghi đè kết quả( sai sót ) từ lần authentication trước
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                // Đã xác thực xong. Nạp hồ sơ (UserDetails) vào "túi" Context.
                // Giúp các tầng sau (Controller/Service) lấy thông tin User ngay lập tức mà không cần parse lại JWT hay gọi DB.
                // Chuyển đổi dữ liệu thô sang dữ liệu có ngữ cảnh
                UserDetails userDetails = detailsService.loadUserForJwt(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                // CẢI THIỆN : Gắn thêm IP/Session ID vào (Quan trọng cho Cybersecurity để truy vết)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //Đóng dấu rằng đã authenticated
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch(Exception e){
            logger.error("Không thể xác thực user: {}", e);
        }

        filterChain.doFilter(request,response);
    }
}
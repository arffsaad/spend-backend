package cyou.arfsd.spendbackend.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cyou.arfsd.spendbackend.Models.User;
import cyou.arfsd.spendbackend.Repositories.UserRepository;

@Component
public class AuthMiddleware implements HandlerInterceptor{
    @Autowired
    private UserRepository userRepository; // Assuming you have a UserRepository

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws IOException {
        System.out.println("Checking token");
        String token = request.getHeader("Token"); // Assuming token is in the header

        // Check if the token exists in the database
        Integer userId = userRepository.findUserIdByToken(token);

        if (userId != null) {
            request.setAttribute("userId", userId);
            // Check if token still valid, if not, kick. If yes, refresh validity every time an action (request) is made.
            User user = userRepository.findById(userId).get();
            user.getValidUntil();
            if (user.getValidUntil().getTime() < System.currentTimeMillis()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                System.out.println("Token expired");
                return false;
            } else {
                // Refresh token validity
                user.setValidUntil(new java.sql.Timestamp(System.currentTimeMillis() + 3600000));
                userRepository.save(user);
                System.out.println("Valid token");
                return true;
            }
            
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            System.out.println("Invalid token");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView model){
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception exception){
    }
}

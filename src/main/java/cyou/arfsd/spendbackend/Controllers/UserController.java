package cyou.arfsd.spendbackend.Controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cyou.arfsd.spendbackend.Models.User;
import cyou.arfsd.spendbackend.Repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/info")
    public Map<String, Object> getUser(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userRepository.findById(userId).get();

        Map<String, Object> response = Map.of(
            "data", user
        );

        return response;
    }
}

package cyou.arfsd.spendbackend.Controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MainController {
    // @Autowired
    // private UserRepository userRepository;

    @RequestMapping(value = "/status", produces = "application/json")
    // return a JSON response
    @ResponseBody public Map<String,Object> status() {
        Map<String,Object> response = Map.of(
            "status", "ok"
        );
        return response;
        // User user = new User();
        // user.setName(name);
        // user.setEmail(email);
        // userRepository.save(user);
        // return "Saved";
    }

    // @GetMapping("/users/all")
    // public @ResponseBody Iterable<User> getAllUsers() {
    //     return userRepository.findAll();
    // }
    
}

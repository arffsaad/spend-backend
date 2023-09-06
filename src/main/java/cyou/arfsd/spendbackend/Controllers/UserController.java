package cyou.arfsd.spendbackend.Controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cyou.arfsd.spendbackend.Models.User;
import cyou.arfsd.spendbackend.Repositories.UserRepository;
import cyou.arfsd.spendbackend.Utils.ResponseHelper;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public Map<String, Object> getUser(@PathVariable(value = "id") Integer id) {
        User user = userRepository.findById(id).get();

        Map<String, Object> response = Map.of(
            "data", user
        );

        return response;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, Object> payload) {
        // Since email must be unique, add checking here to prevent dupes (and prevent ID increment, idk how to do this in models.)
        long existed = userRepository.countByEmail((String) payload.get("email"));
        if (existed >= 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseHelper().returnError("Registration Failed!", "Email exists!"));
        }
        // Initialize new user with name, email, and 0 balance.
        User user = new User();
        user.setName((String) payload.get("name"));
        user.setEmail((String) payload.get("email"));
        // password mana bro
        userRepository.save(user);

        // once we implement proper auth, return auth token here (API token/secret/or wtv the fuck token we use) as to allow auto login after reg
        Map<String, Object> response = Map.of(
            "message", "Register Success!",
            "data", user
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

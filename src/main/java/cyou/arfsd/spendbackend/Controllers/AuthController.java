package cyou.arfsd.spendbackend.Controllers;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cyou.arfsd.spendbackend.Models.User;
import cyou.arfsd.spendbackend.Repositories.UserRepository;
import cyou.arfsd.spendbackend.Utils.HashHelper;
import cyou.arfsd.spendbackend.Utils.ResponseHelper;

@RestController

@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestBody Map<String, Object> payload) {
        long existed = 0;
        String type = (String) payload.get("type");
        if (type.trim().equals("username")) {
            existed = userRepository.countByName((String) payload.get("value"));
        } else if (type.trim().equals("email")) {
            existed = userRepository.countByEmail((String) payload.get("value"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHelper().returnError("failed", "wrong type!"));
        }
        if (existed >= 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHelper().returnError("failed", "exists"));
        }
        Map<String, Object> response = Map.of(
                "status", "success",
                "message", "Please proceed");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String,Object>> verifyToken(@RequestBody Map<String, Object> payload) {
        User user = userRepository.findByToken((String) payload.get("token"));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHelper().returnError("failed", "invalid token"));
        }
        Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        if (now.after(user.getValidUntil())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHelper().returnError("failed", "expired token"));
        }
        Map<String, Object> response = Map.of(
                "status", "success",
                "message", "Please proceed");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, Object> payload) {
        // Since email must be unique, add checking here to prevent dupes (and prevent
        // ID increment, idk how to do this in models.)
        long existed = userRepository.checkExisting((String) payload.get("email"), (String) payload.get("name"));
        if (existed >= 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHelper().returnError("failed", "Email exists!"));
        }
        // Initialize new user with name, email, and 0 balance.
        User user = new User();
        user.setName((String) payload.get("name"));
        user.setEmail((String) payload.get("email"));
        // password section
        String unhashed = (String) payload.get("password");
        HashHelper hashHelper = new HashHelper();
        Map<String, Object> hashed = hashHelper.generateHashed(unhashed);
        user.setPassword(hashed.get("hashed").toString());
        user.setSalt(hashed.get("salt").toString());
        // password end
        userRepository.save(user);

        // once we implement proper auth, return auth token here (API token/secret/or
        // wtv the fuck token we use) as to allow auto login after reg
        Map<String, Object> response = Map.of(
                "status", "success",
                "message", "register success",
                "data", user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        HashHelper hashHelper = new HashHelper();
        User user = userRepository.findByName(((String) payload.get("name")).trim());
        if(hashHelper.auth((String) user.getSalt(), (String) payload.get("password"), user.getPassword())) {
            String sessToken = hashHelper.genToken();
            user.setToken(sessToken);
            Timestamp valid = new java.sql.Timestamp(System.currentTimeMillis()  + 3600000);
            user.setValidUntil(valid);
            userRepository.save(user);
    
            Map<String, Object> response = Map.of(
                "status", "success",
                "message", "login success",
                "data", Map.of(
                    "user", user.getName(),
                    "token", user.getToken(),
                    "id", user.getId(),
                    "email", user.getEmail()
                )
            );
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        else {
            ResponseHelper responseHelper = new ResponseHelper();
            return ResponseEntity.status(HttpStatus.OK).body(responseHelper.returnError("failed", "Username/Password does not match."));
        }
    }

    @RequestMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Token") String token) {
        System.out.println(token);
        User user = userRepository.findByToken(token);
        user.setToken(null);
        user.setValidUntil(null);
        userRepository.save(user);
        Map<String, Object> response = Map.of(
                "status", "success",
                "message", "logout success"
            );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

package cyou.arfsd.spendbackend.Utils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.hash.Hashing;

import cyou.arfsd.spendbackend.Models.User;
import cyou.arfsd.spendbackend.Repositories.UserRepository;

public class HashHelper {

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> generateHashed(String pass) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        String saltedPass = generatedString + pass;
        // SHA256
        String finalHash = Hashing.sha256().hashString(saltedPass, StandardCharsets.UTF_8).toString();
        Map<String, Object> response = Map.of(
                "salt", generatedString,
                "hashed", finalHash);
        return response;
    }

    public boolean auth(String salt, String pass, String hashed) {
        
        try {
            String saltedPass = salt + pass;
            String finalHash = Hashing.sha256().hashString(saltedPass, StandardCharsets.UTF_8).toString();
            if (finalHash.equals(hashed)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String genToken() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 25;
        Random random = new Random();
        String token = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        
        return token;
    }
}

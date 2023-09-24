package cyou.arfsd.spendbackend.Utils;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import com.google.common.hash.Hashing;

public class HashHelper {

    public String generateHashed(String pass) {
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

        return finalHash;
    }

}

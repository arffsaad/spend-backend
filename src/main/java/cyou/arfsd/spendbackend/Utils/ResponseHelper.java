package cyou.arfsd.spendbackend.Utils;

import java.util.Map;

public class ResponseHelper {
    public Map<String, Object> returnError(String message, String reason) {
        Map<String,Object> response = Map.of(
            "message", message,
            "reason", reason
        );

        return response;
    }
}

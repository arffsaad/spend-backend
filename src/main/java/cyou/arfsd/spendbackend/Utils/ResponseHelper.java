package cyou.arfsd.spendbackend.Utils;

import java.util.Map;

public class ResponseHelper {
    public Map<String, Object> returnError(String status, String message) {
        Map<String,Object> response = Map.of(
            "message", message,
            "status", status
        );

        return response;
    }
}

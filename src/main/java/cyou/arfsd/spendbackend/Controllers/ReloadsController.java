package cyou.arfsd.spendbackend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cyou.arfsd.spendbackend.Models.Reloads;
import cyou.arfsd.spendbackend.Models.Wallets;
import cyou.arfsd.spendbackend.Repositories.ReloadsRepository;
import cyou.arfsd.spendbackend.Repositories.WalletsRepository;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Timestamp;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/reloads")
public class ReloadsController {
    @Autowired
    private ReloadsRepository reloadsRepository;

    @Autowired
    private WalletsRepository walletRepository;
    
    private Iterable<Reloads> emptyIterator() {
        return null;
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserReloads(HttpServletRequest request) {
        Integer id = (Integer) request.getAttribute("userId");
        Iterable<Reloads> reloads = emptyIterator();
        try {
            reloads = reloadsRepository.findByUserid(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "status", "oops",
                "message", "no reloads/server error",
                "data", reloads
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
            "status", "success",
            "message", "Reloads Found",
            "data", reloads
        ));
    }
    

    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<Map<String,Object>> createReloads(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Reloads reload = new Reloads();
        reload.setUserid((Integer) request.getAttribute("userId"));
        reload.setAmount((Integer) payload.get("amount"));
        reload.setWalletid((Integer) payload.get("walletid"));
        reload.setRemark((String) payload.get("remark"));
        // set time as default
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        reload.setCreatedtime(currentTime);
        reloadsRepository.save(reload);
        // once reload saved, update wallet balance
        Wallets wallet = walletRepository.findById(reload.getWalletid()).get();
        wallet.setAmount(wallet.getAmount() + reload.getAmount());
        walletRepository.save(wallet);

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "reload created",
            "data", reload
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        
    }
}

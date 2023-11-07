package cyou.arfsd.spendbackend.Controllers;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cyou.arfsd.spendbackend.Models.Reloads;
import cyou.arfsd.spendbackend.Models.Wallets;
import cyou.arfsd.spendbackend.Repositories.ReloadsRepository;
import cyou.arfsd.spendbackend.Repositories.SpendsRepository;
import cyou.arfsd.spendbackend.Repositories.WalletsRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletsController {

    @Autowired
    private WalletsRepository walletRepository;

    @Autowired
    private ReloadsRepository reloadsRepository;

    @Autowired
    private SpendsRepository spendsRepository;

    @GetMapping("/user") // Get list of wallets for user
    public ResponseEntity<Map<String, Object>> getUserWallets(HttpServletRequest request) {
        Integer id = (Integer) request.getAttribute("userId");
        Iterable<Wallets> wallets = null;
        try {
            wallets = walletRepository.findByUserid(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", "oops",
                "message", "no data/server error",
                "data", wallets
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
            "status", "success",
            "message", "wallets retrieved",
            "data", wallets
        ));
    }

    @GetMapping("/{id}") // Get wallet by id
    public ResponseEntity<Map<String, Object>> getWalletById(@PathVariable Integer id, HttpServletRequest request) {
        Optional<Wallets> wallet = walletRepository.findById(id);
        Integer userid = (Integer) request.getAttribute("userId");
        if (!(userid.equals(wallet.get().getUserid()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", "failed",
                "message", "Wallet not found!"
            ));
        }
        List<Map<String, Object>> reloads = walletRepository.fiveReloads(id);
        List<Map<String, Object>> spends = walletRepository.fiveSpends(id);
        Integer sumOfUnfulfilledAmounts = 0;
        if (spendsRepository.UnfulfilledSpendsByWallet(id) != 0) {
            sumOfUnfulfilledAmounts = walletRepository.walletSumOfUnfulfilledAmounts(id);
        }
        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "wallet retrieved",
            "data", Map.of(
                "id", wallet.get().getId(),
                "name", wallet.get().getName(),
                "amount", wallet.get().getAmount(),
                "createdtime", wallet.get().getCreatedtime(),
                "userid", wallet.get().getUserid(),
                "reloads", reloads,
                "spends", spends,
                "unfulfilledAmounts", sumOfUnfulfilledAmounts)
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/create") // Create new wallet
    public ResponseEntity<Map<String, Object>> createWallet(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Wallets wallet = new Wallets();
        wallet.setUserid((Integer) request.getAttribute("userId"));
        wallet.setName((String) payload.get("name"));
        Integer initialAmt = (Integer) payload.get("amount");
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        wallet.setCreatedtime(currentTime);
        wallet.setAmount(initialAmt);
        walletRepository.save(wallet);
        if (initialAmt != 0) {
            Reloads reload = new Reloads();
            reload.setUserid(wallet.getUserid());
            reload.setAmount(initialAmt);
            reload.setRemark("Initial reload for " + wallet.getName());
            reload.setWalletid(wallet.getId());
            reload.setCreatedtime(currentTime);
            reloadsRepository.save(reload);
        }

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "Wallet created!",
            "data", wallet
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

package cyou.arfsd.spendbackend.Controllers;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cyou.arfsd.spendbackend.Models.Reloads;
import cyou.arfsd.spendbackend.Models.Wallets;
import cyou.arfsd.spendbackend.Repositories.ReloadsRepository;
import cyou.arfsd.spendbackend.Repositories.WalletsRepository;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletsController {

    @Autowired
    private WalletsRepository walletRepository;

    @Autowired
    private ReloadsRepository reloadsRepository;

    @GetMapping("/user/{id}") // Get list of wallets for user
    public Iterable<Wallets> getUserWallets(@PathVariable Integer id) {
        return walletRepository.findByUserid(id);
    }

    @GetMapping("/{id}") // Get wallet by id
    public Map<String, Object> getWalletById(@PathVariable Integer id) {
        Optional<Wallets> wallet = walletRepository.findById(id);
        List<Map<String, Object>> reloads = walletRepository.fiveReloads(id);
        List<Map<String, Object>> spends = walletRepository.fiveSpends(id);
        Map<String, Object> response = Map.of(
            "id", wallet.get().getId(),
            "name", wallet.get().getName(),
            "amount", wallet.get().getAmount(),
            "createdtime", wallet.get().getCreatedtime(),
            "userid", wallet.get().getUserid(),
            "reloads", reloads,
            "spends", spends
        );
        return response;
    }

    @PostMapping("/create") // Create new wallet
    public Map<String, Object> createWallet(@RequestBody Map<String, Object> payload) {
        Wallets wallet = new Wallets();
        wallet.setUserid((Integer) payload.get("userid"));
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
            "message", "wallet created",
            "data", wallet
        );

        return response;
    }
}

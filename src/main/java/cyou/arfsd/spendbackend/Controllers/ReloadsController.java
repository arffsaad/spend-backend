package cyou.arfsd.spendbackend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import cyou.arfsd.spendbackend.Models.Reloads;
import cyou.arfsd.spendbackend.Models.Wallets;
import cyou.arfsd.spendbackend.Repositories.ReloadsRepository;
import cyou.arfsd.spendbackend.Repositories.WalletsRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.sql.Timestamp;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/reloads")
public class ReloadsController {
    @Autowired
    private ReloadsRepository reloadsRepository;

    @Autowired
    private WalletsRepository walletRepository;

    @GetMapping("/user/{id}")
    public Iterable<Reloads> getUserReloads(@PathVariable Integer id) {
        return reloadsRepository.findByUserid(id);
    }
    
    @PostMapping(value = "/create", produces = "application/json")
    public @ResponseBody Map<String, Object> createReloads(@RequestBody Map<String, Object> payload) {
        Reloads reload = new Reloads();
        reload.setUserid((Integer) payload.get("userid"));
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

        return response;
        
    }
}

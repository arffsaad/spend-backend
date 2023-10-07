package cyou.arfsd.spendbackend.Controllers;

import java.net.http.HttpRequest;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cyou.arfsd.spendbackend.Models.Spends;
import cyou.arfsd.spendbackend.Models.User;
import cyou.arfsd.spendbackend.Models.Wallets;
import cyou.arfsd.spendbackend.Repositories.SpendsRepository;
import cyou.arfsd.spendbackend.Repositories.UserRepository;
import cyou.arfsd.spendbackend.Repositories.WalletsRepository;
import cyou.arfsd.spendbackend.Utils.MinioHelper;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/spending")
public class SpendsController {
    @Autowired
    private SpendsRepository spendsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletsRepository walletRepository;

    @GetMapping("/{spendingid}")
    public Spends getSpendsById(@PathVariable("spendingid") Integer spendingid, HttpServletRequest request ) {
        Integer userid = (Integer) request.getAttribute("userId");
        Spends spend = spendsRepository.findById(spendingid).get();
        if (!(userid.equals(spend.getUserid()))) {
            return null; // TODO : fix return as response something and return err
        }
        else {
            return spend;
        }
    }

    @GetMapping("/user")
    public Map<String,Object> getSpendsByUserId(HttpServletRequest request) {
        Integer id = (Integer) request.getAttribute("userId");
        List<Map<String, Object>> spends = spendsRepository.userSummary(id);
        Integer sumOfUnfulfilledAmounts = 0;
        if (spendsRepository.UnfulfilledSpends() != 0) {
            sumOfUnfulfilledAmounts = spendsRepository.getSumOfUnfulfilledAmounts(id);
        }
        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "spendings retrieved",
            "data", spends,
            "UnfulfilledAmount", sumOfUnfulfilledAmounts
        );
        return response;
    }

    @PostMapping("/create") // TODO : Change request accept as Formdata to accommodate for image upload
    public @ResponseBody Map<String, Object> createSpends(
        HttpServletRequest request, 
        @RequestParam("amount") Integer amount, 
        @RequestParam("remark") String remark,
        @RequestParam("walletid") Integer walletid,
        @RequestParam("fulfilled") Boolean fulfilled, 
        @RequestParam(value = "receipt", required = false) MultipartFile receipt) {
        Spends spends = new Spends();
        spends.setUserid((Integer) request.getAttribute("userId"));
        spends.setAmount(amount);
        spends.setRemark(remark);
        spends.setWalletid(walletid);
        
        Wallets wallet = walletRepository.findById(spends.getWalletid()).get();
        User user = userRepository.findById(spends.getUserid()).get();
        
        if (fulfilled) {
            if (wallet.getAmount() < spends.getAmount()) {
                Map<String, Object> response = Map.of(
                    "status", "failed",
                    "message", "insufficient balance"
                );
                return response;
            }
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            spends.setFulfilled_at(currentTime);
            wallet.setAmount(wallet.getAmount() - spends.getAmount());
        }
        else {
            spends.setFulfilled_at(null);
            
        }
        if (receipt != null) {
            MinioHelper minioHelper = new MinioHelper("http://127.0.0.1:9000", "spend-bucket", user.getName());
            Map<String, Object> uploadResponse = minioHelper.UploadFile(receipt, "spend-bucket", user.getName());
            spends.setRecslug( "/" + uploadResponse.get("fileName").toString());
        }
        else {
            spends.setRecslug(null);
        }
        spendsRepository.save(spends);
        walletRepository.save(wallet);

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "spending created",
            "data", spends
        );

        return response;
    }

    @PostMapping("/fulfill/{id}")
    public @ResponseBody Map<String, Object> fulfillSpends(@PathVariable("id") Integer id, HttpServletRequest request) {
        Spends spends = spendsRepository.findById(id).get();
        if (!(spends.getUserid().equals((Integer) request.getAttribute("userId")))) {
            Map<String, Object> response = Map.of(
                "status", "failed",
                "message", "unauthorized action!"
            );
            return response;
        }
        if (spends.getFulfilled_at() == null) {
            Wallets wallet = walletRepository.findById(spends.getWalletid()).get();
            if (wallet.getAmount() < spends.getAmount()) {
                Map<String, Object> response = Map.of(
                    "status", "failed",
                    "message", "insufficient balance"
                );
                return response;
            }
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            spends.setFulfilled_at(currentTime);
            spendsRepository.save(spends);
            wallet.setAmount(wallet.getAmount() - spends.getAmount());
            walletRepository.save(wallet);
        }

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "spending fulfilled",
            "data", spends
        );

        return response;
    }

    @PatchMapping("/update/{id}")
    public @ResponseBody Map<String, Object> updateSpends(@PathVariable("id") Integer id, @RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Spends spends = spendsRepository.findById(id).get();
        if (!(spends.getUserid().equals((Integer) request.getAttribute("userId")))) {
            Map<String, Object> response = Map.of(
                "status", "failed",
                "message", "unauthorized action!"
            );
            return response;
        }
        if (payload.containsKey("amount")) {
            if (spends.getFulfilled_at() != null) {
                Wallets wallet = walletRepository.findById(spends.getWalletid()).get();
                wallet.setAmount(wallet.getAmount() + spends.getAmount());
                wallet.setAmount(wallet.getAmount() - (Integer) payload.get("amount"));
                walletRepository.save(wallet);
            }
            spends.setAmount((Integer) payload.get("amount"));
        }
        if (payload.containsKey("remark")) {
            spends.setRemark((String) payload.get("remark"));
        }
        spendsRepository.save(spends);

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "spending updated",
            "data", spends
        );

        return response;
    }

    @PostMapping("/reverse/{id}")
    public @ResponseBody Map<String, Object> reverseSpends(@PathVariable("id") Integer id, HttpServletRequest request) {
        Spends spends = spendsRepository.findById(id).get();
        if (!(spends.getUserid().equals((Integer) request.getAttribute("userId")))) {
            Map<String, Object> response = Map.of(
                "status", "failed",
                "message", "unauthorized action!"
            );
            return response;
        }
        if (spends.getFulfilled_at() != null) {
            spends.setFulfilled_at(null);
            spendsRepository.save(spends);
            Wallets wallet = walletRepository.findById(spends.getWalletid()).get();
            wallet.setAmount(wallet.getAmount() + spends.getAmount());
            walletRepository.save(wallet);
        }
        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "spending reversed",
            "data", spends
        );

        return response;
    }
}

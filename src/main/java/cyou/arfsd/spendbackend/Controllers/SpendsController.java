package cyou.arfsd.spendbackend.Controllers;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cyou.arfsd.spendbackend.Models.Spends;
import cyou.arfsd.spendbackend.Models.User;
import cyou.arfsd.spendbackend.Repositories.SpendsRepository;
import cyou.arfsd.spendbackend.Repositories.UserRepository;

@RestController
@RequestMapping("/api/v1/spending")
public class SpendsController {
    @Autowired
    private SpendsRepository spendsRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{spendingid}")
    public Spends getSpendsById(@PathVariable("spendingid") Integer spendingid) {
        // TODO : handle exception if not found
        return spendsRepository.findById(spendingid).get();
    }

    @GetMapping("/user/{id}")
    public Map<String,Object> getSpendsByUserId(@PathVariable("id") Integer id) {
        Iterable<Spends> spends = spendsRepository.findByUserid(id);
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
    public @ResponseBody Map<String, Object> createSpends(@RequestBody Map<String, Object> payload) {
        Spends spends = new Spends();
        spends.setUserid((Integer) payload.get("userid"));
        spends.setAmount((Integer) payload.get("amount"));
        spends.setRemark((String) payload.get("remark"));
        // TODO : implement minio here and get the slug for file.
        spends.setRecslug("exampleSlug");
        // END TODO
        if ((Boolean) payload.get("fulfilled") == true) {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            spends.setFulfilled_at(currentTime);
            spendsRepository.save(spends);
            User user = userRepository.findById(spends.getUserid()).get();
            user.setBalance(user.getBalance() - spends.getAmount());
            userRepository.save(user);
        }
        else {
            spends.setFulfilled_at(null);
            spendsRepository.save(spends);
        }

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "spending created",
            "data", spends
        );

        return response;
    }

    @PostMapping("/fulfill/{id}")
    public @ResponseBody Map<String, Object> fulfillSpends(@PathVariable("id") Integer id) {
        Spends spends = spendsRepository.findById(id).get();
        if (spends.getFulfilled_at() == null) {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            spends.setFulfilled_at(currentTime);
            spendsRepository.save(spends);
            User user = userRepository.findById(spends.getUserid()).get();
            user.setBalance(user.getBalance() - spends.getAmount());
            userRepository.save(user);
        }

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "spending fulfilled",
            "data", spends
        );

        return response;
    }

    @PatchMapping("/update/{id}")
    public @ResponseBody Map<String, Object> updateSpends(@PathVariable("id") Integer id, @RequestBody Map<String, Object> payload) {
        Spends spends = spendsRepository.findById(id).get();
        if (payload.containsKey("amount")) {
            if (spends.getFulfilled_at() != null) {
                User user = userRepository.findById(spends.getUserid()).get();
                user.setBalance(user.getBalance() + spends.getAmount());
                user.setBalance(user.getBalance() - (Integer) payload.get("amount"));
                userRepository.save(user);
            }
            spends.setAmount((Integer) payload.get("amount"));
        }
        if (payload.containsKey("remark")) {
            spends.setRemark((String) payload.get("remark"));
        }
        if (payload.containsKey("recslug")) {
            spends.setRecslug((String) payload.get("recslug"));
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
    public @ResponseBody Map<String, Object> reverseSpends(@PathVariable("id") Integer id) {
        Spends spends = spendsRepository.findById(id).get();
        spends.setFulfilled_at(null);
        spendsRepository.save(spends);
        User user = userRepository.findById(spends.getUserid()).get();
        user.setBalance(user.getBalance() + spends.getAmount());
        userRepository.save(user);

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "spending reversed",
            "data", spends
        );

        return response;
    }
}

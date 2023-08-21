package cyou.arfsd.spendbackend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import cyou.arfsd.spendbackend.Models.Reloads;
import cyou.arfsd.spendbackend.Models.User;
import cyou.arfsd.spendbackend.Repositories.ReloadsRepository;
import cyou.arfsd.spendbackend.Repositories.UserRepository;

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
    private UserRepository userRepository;

    @GetMapping("/user/{id}")
    public Iterable<Reloads> getUserReloads(@PathVariable Integer id) {
        return reloadsRepository.findByUserid(id);
    }
    
    @PostMapping(value = "/create", produces = "application/json")
    public @ResponseBody Map<String, Object> createReloads(@RequestBody Map<String, Object> payload) {
        Reloads reload = new Reloads();
        reload.setUserid((Integer) payload.get("userid"));
        reload.setAmount((Integer) payload.get("amount"));
        reload.setRemark((String) payload.get("remark"));
        // set time as default
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        reload.setCreatedtime(currentTime);
        reloadsRepository.save(reload);
        // once reload saved, update user balance
        User user = userRepository.findById(reload.getUserid()).get();
        user.setBalance(user.getBalance() + reload.getAmount());
        userRepository.save(user);

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "reload created",
            "data", reload
        );

        return response;
        
    }
}

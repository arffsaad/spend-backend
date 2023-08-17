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
import cyou.arfsd.spendbackend.Repositories.SpendsRepository;

@RestController
@RequestMapping("/api/v1/spending")
public class SpendsController {
    @Autowired
    private SpendsRepository spendsRepository;

    @GetMapping("/user/{id}")
    public Iterable<Spends> getSpendsByUserId(@PathVariable("id") Integer id) {
        return spendsRepository.findByUserid(id);
    }

    @GetMapping("/{id}")
    public Spends getSpendsById(@PathVariable("id") Integer id) {
        return spendsRepository.findById(id).get();
    }

    @PostMapping("/create")
    public @ResponseBody Map<String, Object> createSpends(@RequestBody Map<String, Object> payload) {
        Spends spends = new Spends();
        spends.setUserid((Integer) payload.get("userid"));
        spends.setAmount((Integer) payload.get("amount"));
        spends.setRemark((String) payload.get("remark"));
        // TODO : implement minio here and get the slug for file.
        spends.setRecslug("exampleSlug");
        // END TODO
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        spends.setFulfilled_at(currentTime); 
        spendsRepository.save(spends);

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "spending created",
            "data", spends
        );

        return response;
    }

    @PatchMapping("/update/{id}")
    public @ResponseBody Map<String, Object> updateSpends(@PathVariable("id") Integer id, @RequestBody Map<String, Object> payload) {
        Spends spends = spendsRepository.findById(id).get();
        if (payload.containsKey("amount")) {
            spends.setAmount((Integer) payload.get("amount"));
        }
        if (payload.containsKey("remark")) {
            spends.setRemark((String) payload.get("remark"));
        }
        if (payload.containsKey("recslug")) {
            spends.setRecslug((String) payload.get("recslug"));
        }
        if (payload.containsKey("fulfilled_at")) {
            spends.setFulfilled_at(Timestamp.valueOf((String) payload.get("fulfilled_at")));
        }
        spendsRepository.save(spends);

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "spending updated",
            "data", spends
        );

        return response;
    }
}

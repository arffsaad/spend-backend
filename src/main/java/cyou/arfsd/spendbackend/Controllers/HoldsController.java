package cyou.arfsd.spendbackend.Controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cyou.arfsd.spendbackend.Models.Holds;
import cyou.arfsd.spendbackend.Repositories.HoldsRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/holds")
public class HoldsController {
    
    @Autowired
    private HoldsRepository holdsRepository;
    
    @GetMapping("/user/{id}")
    public Map<String, Object> getHoldsByUserId(@PathVariable(value = "id") Integer userId) {
        Iterable<Holds> holds = holdsRepository.findByUserid(userId);

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "holds retrieved",
            "data", holds
        );

        return response;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getHoldById(@PathVariable(value = "id") Integer id) {
        Holds holds = holdsRepository.findById(id).get();

        Map<String, Object> response = Map.of(
            "data", holds
        );

        return response;
    }

    @PostMapping("/create")
    public Map<String, Object> createHold(@RequestBody Map<String, Object> payload) {
        Holds holds = new Holds();
        holds.setUserid((Integer) payload.get("userid"));
        holds.setName((String) payload.get("name"));
        holds.setAmount((Integer) payload.get("amount"));
        holds.setRemark((String) payload.get("remark"));

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "hold created",
            "data", holds
        );

        return response;
    }

    @PatchMapping("/update/{id}")
    public @ResponseBody Map<String, Object> updateHolds (@PathVariable(value = "id") Integer id, @RequestBody Map<String, Object> payload) {
        Holds holds = holdsRepository.findById(id).get();
        if (payload.containsKey("name")) {
            holds.setName((String) payload.get("name"));
        }
        if (payload.containsKey("amount")) {
            holds.setAmount((Integer) payload.get("amount"));
        }
        if (payload.containsKey("remark")) {
            holds.setRemark((String) payload.get("remark"));
        }
        holdsRepository.save(holds);

        Map<String, Object> response = Map.of(
            "data", holds
        );

        return response;
    }
    
}

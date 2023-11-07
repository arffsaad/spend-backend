package cyou.arfsd.spendbackend.Controllers;

import java.sql.Timestamp;
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


import cyou.arfsd.spendbackend.Models.Instalments;
import cyou.arfsd.spendbackend.Models.Spends;
import cyou.arfsd.spendbackend.Models.Wallets;
import cyou.arfsd.spendbackend.Repositories.InstalmentsRepository;
import cyou.arfsd.spendbackend.Repositories.SpendsRepository;
import cyou.arfsd.spendbackend.Repositories.WalletsRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/instalments")
public class InstalmentsController {
    
    @Autowired
    private InstalmentsRepository instalments;

    @Autowired
    private WalletsRepository wallets;

    @Autowired
    private SpendsRepository spends;

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getInstalmentsByUserId(HttpServletRequest request) {
        Integer id = (Integer) request.getAttribute("userId");
        Iterable<Instalments> instalment = instalments.findByUserid(id);
        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "instalments retrieved",
            "data", instalment
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getInstalmentsById(@PathVariable Integer id, HttpServletRequest request) {
        Optional<Instalments> instalment = instalments.findById(id);
        Integer userid = (Integer) request.getAttribute("userId");
        if (!(userid.equals(instalment.get().getUserid()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", "failed",
                "message", "Not Found!"
            ));
        }
        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "instalment retrieved",
            "data", instalments
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createInstalment(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Integer id = (Integer) request.getAttribute("userId");
        Instalments instalment = new Instalments();
        instalment.setUserid((Integer) id);
        instalment.setName((String) payload.get("name"));
        instalment.setAmountLeft((Integer) payload.get("totalAmt"));
        instalment.setAmountDue(0);
        instalment.setMonths((Integer) payload.get("months"));
        instalment.setDueDate((Integer) payload.get("dueDate"));
        instalments.save(instalment);
        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "instalment created",
            "data", instalment
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Map<String, Object>> payInstalment(@PathVariable Integer id, @RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Optional<Instalments> instalment = instalments.findById(id);
        Integer userid = (Integer) request.getAttribute("userId");
        if (!(userid.equals(instalment.get().getUserid()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", "failed",
                "message", "Not Found!"
            ));
        }
        // get the amount paid and wallet used
        Integer amountPaid = (Integer) payload.get("amountPaid");
        Integer walletId = (Integer) payload.get("walletId");

        // check if wallet has enough balance. if not, kick.
        Wallets wallet = wallets.findById(walletId).get();
        // intermediary check if wallet is owned by user
        if (!(wallet.getUserid().equals(userid))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", "failed",
                "message", "Not Found!"
            ));
        }

        if (wallet.getAmount() < amountPaid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", "failed",
                "message", "Insufficient Balance!"
            ));
        }

        // part 1 : performing calculations on instalment object
        instalment.get().setAmountDue(instalment.get().getAmountDue() - amountPaid);
        instalments.save(instalment.get());

        // part 2 : deduction from wallet

        wallet.setAmount(wallet.getAmount() - amountPaid);
        wallets.save(wallet);

        // part 3 : creation of spending record

        Spends spend = new Spends();
        spend.setUserid((Integer) request.getAttribute("userId"));
        spend.setAmount(amountPaid);
        spend.setRemark("Instalment payment for " + instalment.get().getName());
        spend.setWalletid(wallet.getId());
        spend.setRecslug(null);

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        spend.setFulfilled_at(currentTime);
        spends.save(spend);

        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "Instalment Paid"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

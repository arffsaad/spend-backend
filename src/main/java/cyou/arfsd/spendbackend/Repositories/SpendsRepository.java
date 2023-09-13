package cyou.arfsd.spendbackend.Repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cyou.arfsd.spendbackend.Models.Spends;

public interface SpendsRepository extends CrudRepository<Spends, Integer> {
    Iterable<Spends> findByUserid(Integer userId);

    // Query to get SUM of all amounts where fulfilled at is null
    @Query(value = "SELECT SUM(amount) FROM spends WHERE fulfilled_at IS NULL AND userid = ?1", nativeQuery = true)
    Integer getSumOfUnfulfilledAmounts(Integer userId);

    @Query(value = "SELECT COUNT(*) FROM spends WHERE fulfilled_at IS NULL", nativeQuery = true)
    Integer UnfulfilledSpends();

    @Query(value = "SELECT s.*, w.name AS wallet FROM spends s LEFT JOIN wallets w ON w.id = s.walletid WHERE s.userid = ?1", nativeQuery = true)
    List<Map<String, Object>> userSummary(Integer userId);
}

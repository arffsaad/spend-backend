package cyou.arfsd.spendbackend.Repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cyou.arfsd.spendbackend.Models.Wallets;

public interface WalletsRepository extends CrudRepository<Wallets, Integer> {
    Iterable<Wallets> findByUserid(Integer userId);

    // find by id
    Optional<Wallets> findById(Integer id);

    @Query(value = "SELECT s.*, w.name AS wallet FROM spends s LEFT JOIN wallets w ON w.id = s.walletid WHERE s.userid = ?1", nativeQuery = true)
    List<Map<String, Object>> userSummary(Integer userId);

    @Query(value = "SELECT * FROM spends WHERE walletid =?1 ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<Map<String, Object>> fiveSpends(Integer walletId);

    @Query(value = "SELECT * FROM reloads WHERE walletid =?1 ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<Map<String, Object>> fiveReloads(Integer walletId);
}

package cyou.arfsd.spendbackend.Repositories;

import org.springframework.data.repository.CrudRepository;

import cyou.arfsd.spendbackend.Models.Wallets;

public interface WalletsRepository extends CrudRepository<Wallets, Integer> {
    Iterable<Wallets> findByUserid(Integer userId);
}

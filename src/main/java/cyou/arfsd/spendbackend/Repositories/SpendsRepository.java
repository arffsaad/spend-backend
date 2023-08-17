package cyou.arfsd.spendbackend.Repositories;

import org.springframework.data.repository.CrudRepository;

import cyou.arfsd.spendbackend.Models.Spends;

public interface SpendsRepository extends CrudRepository<Spends, Integer> {
    Iterable<Spends> findByUserid(Integer userId);
}

package cyou.arfsd.spendbackend.Repositories;

import org.springframework.data.repository.CrudRepository;

import cyou.arfsd.spendbackend.Models.Holds;

public interface HoldsRepository extends CrudRepository<Holds, Integer> {
    Iterable<Holds> findByUserid(Integer userId);
}

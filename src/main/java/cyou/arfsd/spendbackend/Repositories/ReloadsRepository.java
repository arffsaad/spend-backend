package cyou.arfsd.spendbackend.Repositories;

import org.springframework.data.repository.CrudRepository;

import cyou.arfsd.spendbackend.Models.Reloads;

public interface ReloadsRepository extends CrudRepository<Reloads, Long> {
    Iterable<Reloads> findByUserid(Integer userId);
}

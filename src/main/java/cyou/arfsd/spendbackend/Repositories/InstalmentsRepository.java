package cyou.arfsd.spendbackend.Repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cyou.arfsd.spendbackend.Models.Instalments;

public interface InstalmentsRepository extends CrudRepository<Instalments, Integer> {
    Optional<Instalments> findByid(Integer userId);

    Iterable<Instalments> findByUserid(Integer userId);
}

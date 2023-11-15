package cyou.arfsd.spendbackend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cyou.arfsd.spendbackend.Models.Instalments;

public interface InstalmentsRepository extends CrudRepository<Instalments, Integer> {
    Optional<Instalments> findByid(Integer userId);

    Iterable<Instalments> findByUserid(Integer userId);

    @Query(value = "SELECT * FROM instalments WHERE months > 0", nativeQuery = true)
    List<Instalments> findAllIncomplete();

}

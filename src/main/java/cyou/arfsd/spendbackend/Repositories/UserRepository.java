package cyou.arfsd.spendbackend.Repositories;

import org.springframework.data.repository.CrudRepository;

import cyou.arfsd.spendbackend.Models.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByEmail(String email);

    Long countByEmail(String email);
}

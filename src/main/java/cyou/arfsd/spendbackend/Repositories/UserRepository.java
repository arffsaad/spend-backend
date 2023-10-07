package cyou.arfsd.spendbackend.Repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cyou.arfsd.spendbackend.Models.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByEmail(String email);

    @Query(value = "SELECT * FROM user WHERE name = ?1", nativeQuery = true)
    User findByName(String name);

    @Query(value = "SELECT * FROM user WHERE token = ?1", nativeQuery = true)
    User findByToken(String token);

    Long countByEmail(String email);

    Long countByName(String name);

    @Query(value = "SELECT id FROM user WHERE token = ?1", nativeQuery = true)
    Integer findUserIdByToken(String token);

    @Query(value = "SELECT COUNT(*) FROM user WHERE email = ?1 OR name = ?2", nativeQuery = true)
    Long checkExisting(String email, String name);
}

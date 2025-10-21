package CS203G3.tariff_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import CS203G3.tariff_backend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByUuid(String uuid);
}

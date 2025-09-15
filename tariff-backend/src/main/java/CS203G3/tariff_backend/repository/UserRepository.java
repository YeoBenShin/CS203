package CS203G3.tariff_backend.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import CS203G3.tariff_backend.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
}

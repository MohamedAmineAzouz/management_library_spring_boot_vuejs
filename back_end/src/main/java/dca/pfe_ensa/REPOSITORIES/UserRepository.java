package dca.pfe_ensa.REPOSITORIES;
import dca.pfe_ensa.MODEL.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository <Users, Integer> {
    Optional<Users> findByUserName(String userName);

}

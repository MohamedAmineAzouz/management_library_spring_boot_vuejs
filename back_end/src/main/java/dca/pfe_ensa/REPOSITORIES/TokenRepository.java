package dca.pfe_ensa.REPOSITORIES;
import dca.pfe_ensa.MODEL.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
        select t from Token t inner join Users u on t.user.userId = u.userId
        where t.user.userId = :userId and t.loggedOut = false
    """)
    List<Token> findAllAccessTokensByUser(Integer userId);

    Optional<Token> findByAccessToken(String token);

    Optional<Token > findByRefreshToken(String token);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.user.userId = :userId")
    void deleteByUserId(Integer userId);
}
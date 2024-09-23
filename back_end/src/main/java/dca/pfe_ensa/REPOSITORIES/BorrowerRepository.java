package dca.pfe_ensa.REPOSITORIES;
import dca.pfe_ensa.MODEL.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, Integer> {
    List<Borrower> findByUser_UserIdAndBook_BookId(int userId, int bookId);

    @Query("SELECT b FROM Borrower b WHERE b.user.userName = :username")
    List<Borrower> findByUserUsername(@Param("username") String username);

    List<Borrower> findBorrowerByBookBookId(int bookId);

    List<Borrower> findByUserUserId(int id);
}

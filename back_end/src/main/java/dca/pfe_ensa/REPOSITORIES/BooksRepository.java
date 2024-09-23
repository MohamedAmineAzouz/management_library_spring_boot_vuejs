package dca.pfe_ensa.REPOSITORIES;
import dca.pfe_ensa.MODEL.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BooksRepository extends JpaRepository<Books, Integer> {
    Optional<Books> findByBookId(int bookId);
}

package dca.pfe_ensa.REPOSITORIES;
import dca.pfe_ensa.MODEL.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface PictureRepository extends JpaRepository<Picture, Integer> {

    @Query("SELECT p FROM Picture p WHERE p.book.bookId = :bookId")
    Optional<Picture> findPictureByBookId(@Param("bookId") int bookId);

    @Modifying
    @Query("DELETE FROM Picture p WHERE p.book.bookId = :bookId")
    void deleteByBookId(@Param("bookId") int bookId);

}

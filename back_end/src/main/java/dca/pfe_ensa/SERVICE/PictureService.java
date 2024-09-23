package dca.pfe_ensa.SERVICE;
import dca.pfe_ensa.REPOSITORIES.PictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PictureService {

    @Autowired
    private PictureRepository pictureRepository;

    @Transactional
    public void deletePictureByBookId(int bookId) {
        pictureRepository.deleteByBookId(bookId);
    }
}

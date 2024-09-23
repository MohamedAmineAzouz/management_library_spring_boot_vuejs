package dca.pfe_ensa.SERVICE;
import dca.pfe_ensa.MODEL.Borrower;
import dca.pfe_ensa.REPOSITORIES.BorrowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    @Autowired
    public BorrowerService(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    public List<Borrower> getAllBorrowers() {
        return borrowerRepository.findAll();
    }

    public Borrower getBorrowerById(int id) {
        Optional<Borrower> borrower = borrowerRepository.findById(id);
        return borrower.orElseThrow(() -> new RuntimeException("Borrower not found with id " + id));
    }

    public Borrower createBorrower(Borrower borrower) {
        return borrowerRepository.save(borrower);
    }

    public List<Borrower> getBorrowersByUsername(String username) {
        return borrowerRepository.findByUserUsername(username);
    }

    public Borrower updateBorrower(int id, Borrower borrowerDetails) {
        Borrower existingBorrower = getBorrowerById(id);
        existingBorrower.setConfirmReturn(borrowerDetails.isConfirmReturn());
        return borrowerRepository.save(existingBorrower);
    }

    public void deleteBorrower(int id) {
        Borrower borrower = getBorrowerById(id);
        borrowerRepository.delete(borrower);
    }

    public List<Borrower> findByUserIdAndBookId(int userId, int bookId) {
        return borrowerRepository.findByUser_UserIdAndBook_BookId(userId, bookId);
    }
}

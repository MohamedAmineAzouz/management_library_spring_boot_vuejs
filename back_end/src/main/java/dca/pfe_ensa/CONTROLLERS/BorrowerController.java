package dca.pfe_ensa.CONTROLLERS;
import dca.pfe_ensa.MODEL.Borrower;
import dca.pfe_ensa.MODEL.Users;
import dca.pfe_ensa.SERVICE.BorrowerService;
import dca.pfe_ensa.SERVICE.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/borrowers")
@CrossOrigin(origins = "http://localhost:3000")
public class BorrowerController {

    private final UserService usersService;

    private final BorrowerService borrowerService;

    public BorrowerController(UserService usersService, BorrowerService borrowerService) {
        this.usersService = usersService;
        this.borrowerService = borrowerService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Borrower>> getAllBorrowers() {
        List<Borrower> borrowers = borrowerService.getAllBorrowers();
        return ResponseEntity.ok(borrowers);
    }

        @GetMapping("/getById/{id}")
    public ResponseEntity<Borrower> getBorrowerById(@PathVariable int id) {
        Borrower borrower = borrowerService.getBorrowerById(id);
        return ResponseEntity.ok(borrower);
    }

    @GetMapping("/getByUsername/{username}")
    public ResponseEntity<List<Borrower>> getBorrowersByUsername(@PathVariable String username) {
        List<Borrower> borrowers = borrowerService.getBorrowersByUsername(username);
        if (borrowers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(borrowers);
    }

    @PostMapping("/add")
    public ResponseEntity<?> createBorrower(@RequestBody Borrower borrowerRequest) {
        Integer userId = usersService.getUserIdByUsername(borrowerRequest.getUser().getUsername());

        if (userId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Borrower> existingBorrowers = borrowerService.findByUserIdAndBookId(userId, borrowerRequest.getBook().getBookId());
        for (Borrower existingBorrower : existingBorrowers) {
            if (!existingBorrower.isConfirmReturn()) {
                return ResponseEntity.badRequest().body("You have already borrowed this book and haven't returned it yet.");
            }
        }

        Users user = new Users();
        user.setUserId(userId);
        user.setRole(borrowerRequest.getUser().getRole());

        Borrower borrower = new Borrower();
        borrower.setBorrowerDate(borrowerRequest.getBorrowerDate());
        borrower.setReturnedDate(borrowerRequest.getReturnedDate());
        borrower.setBook(borrowerRequest.getBook());
        borrower.setUser(user);
        borrower.setConfirmReturn(false);

        Borrower newBorrower = borrowerService.createBorrower(borrower);
        return ResponseEntity.ok(newBorrower);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Borrower> updateBorrower(@PathVariable int id, @RequestBody Borrower borrowerDetails) {
        Borrower updatedBorrower = borrowerService.updateBorrower(id, borrowerDetails);
        return ResponseEntity.ok(updatedBorrower);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrower(@PathVariable int id) {
        borrowerService.deleteBorrower(id);
        return ResponseEntity.noContent().build();
    }
}

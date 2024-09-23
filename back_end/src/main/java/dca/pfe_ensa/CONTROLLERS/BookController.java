package dca.pfe_ensa.CONTROLLERS;
import com.fasterxml.jackson.databind.ObjectMapper;
import dca.pfe_ensa.MODEL.Books;
import dca.pfe_ensa.MODEL.Borrower;
import dca.pfe_ensa.MODEL.Picture;
import dca.pfe_ensa.REPOSITORIES.BooksRepository;
import dca.pfe_ensa.REPOSITORIES.BorrowerRepository;
import dca.pfe_ensa.REPOSITORIES.PictureRepository;
import dca.pfe_ensa.SERVICE.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {
    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BorrowerRepository borrowerRepository;

    @PostMapping("/add")
    public ResponseEntity<Books> createBook(
            @RequestParam("book") String bookJson,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Books book = objectMapper.readValue(bookJson, Books.class);
            System.out.println(book);
            if (file != null && !file.isEmpty()) {
                String filePath = saveFile(file);

                Picture picture = new Picture();
                picture.setPath(filePath);
                picture.setBook(book);

                booksRepository.save(book);
                pictureRepository.save(picture);
            } else {
                booksRepository.save(book);
            }

            return ResponseEntity.ok(book);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "src/main/resources/static/uploads/";
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return "/uploads/" + fileName;
    }


    @GetMapping("/all")
    public List<Books> getAllBooks() {
        return booksRepository.findAll();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Books> getBookById(@PathVariable int id) {
        Optional<Books> book = booksRepository.findByBookId(id);
        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Books> updateBook(
            @PathVariable int id,
            @RequestParam("book") String bookDetails,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Books book = objectMapper.readValue(bookDetails, Books.class);

            Optional<Books> existingBookOpt = booksRepository.findById(id);
            if (existingBookOpt.isPresent()) {
                Books existingBook = existingBookOpt.get();

                existingBook.setTitle(book.getTitle());
                existingBook.setAuthor(book.getAuthor());
                existingBook.setYear(book.getYear());
                existingBook.setNumberPages(book.getNumberPages());
                existingBook.setType(book.getType());
                existingBook.setQuantity(book.getQuantity());

                if (file != null && !file.isEmpty()) {
                    String filePath = saveFile(file);
                    Picture newPicture = new Picture();
                    newPicture.setPath(filePath);
                    newPicture.setBook(existingBook);

                    pictureRepository.save(newPicture);
                    existingBook.setPicture(newPicture);
                }
                booksRepository.save(existingBook);
                return ResponseEntity.ok(existingBook);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        try {
            List<Borrower> borrowers = borrowerRepository.findBorrowerByBookBookId(id);
            borrowerRepository.deleteAll(borrowers);

            Optional<Books> book = booksRepository.findById(id);
            if (book.isPresent()) {
                booksRepository.delete(book.get());
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

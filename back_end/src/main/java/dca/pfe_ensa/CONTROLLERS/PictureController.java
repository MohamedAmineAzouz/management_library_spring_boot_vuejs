package dca.pfe_ensa.CONTROLLERS;

import dca.pfe_ensa.MODEL.Picture;
import dca.pfe_ensa.REPOSITORIES.PictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/pictures")
@CrossOrigin(origins = "http://localhost:3000")
public class PictureController {

    @Autowired
    private PictureRepository pictureRepository;

    @DeleteMapping("/deleteByBookId/{bookId}")
    @Transactional
    public ResponseEntity<String> deletePictureByBookId(@PathVariable int bookId) {
        try {
            Optional<Picture> pictureOpt = pictureRepository.findPictureByBookId(bookId);
            if (pictureOpt.isPresent()) {
                Picture picture = pictureOpt.get();
                Path filePath = Paths.get("src/main/resources/static" + picture.getPath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }

                pictureRepository.deleteByBookId(bookId);
                return ResponseEntity.ok("Picture deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Picture not found");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting picture");
        }
    }
}

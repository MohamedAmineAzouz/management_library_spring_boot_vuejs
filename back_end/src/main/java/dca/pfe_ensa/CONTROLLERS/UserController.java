package dca.pfe_ensa.CONTROLLERS;
import dca.pfe_ensa.MODEL.Borrower;
import dca.pfe_ensa.MODEL.Users;
import dca.pfe_ensa.REPOSITORIES.BorrowerRepository;
import dca.pfe_ensa.REPOSITORIES.UserRepository;
import dca.pfe_ensa.SERVICE.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private BorrowerRepository borrowerRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public UserController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/all")
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userId", user.getUserId());
            userMap.put("firstname", user.getFirstname());
            userMap.put("lastname", user.getLastname());
            userMap.put("email", user.getEmail());
            userMap.put("gsm", user.getGsm());
            userMap.put("address", user.getAddress());
            userMap.put("cne", user.getCne());
            userMap.put("sector", user.getSector());
            userMap.put("userName", user.getUsername());
            userMap.put("role", user.getRole());
            return userMap;
        }).collect(Collectors.toList());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable int id) {

        Optional<Users> user = userRepository.findById(id);
        if (user.isPresent()) {
            Users u = user.get();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userId", u.getUserId());
            userMap.put("firstname", u.getFirstname());
            userMap.put("lastname", u.getLastname());
            userMap.put("email", u.getEmail());
            userMap.put("gsm", u.getGsm());
            userMap.put("address", u.getAddress());
            userMap.put("cne", u.getCne());
            userMap.put("sector", u.getSector());
            userMap.put("userName", u.getUsername());
            userMap.put("role", u.getRole());
            return ResponseEntity.ok(userMap);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> createUser(@RequestBody Users user) {

        if (userRepository.findByUserName(user.getUsername()).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "This username already exists");
            return ResponseEntity.badRequest().body(response);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable int id, @RequestBody Users userDetails) {

        Optional<Users> user = userRepository.findById(id);
        if (user.isPresent()) {
            Users updatedUser = user.get();
            updatedUser.setFirstname(userDetails.getFirstname());
            updatedUser.setLastname(userDetails.getLastname());
            updatedUser.setEmail(userDetails.getEmail());
            updatedUser.setGsm(userDetails.getGsm());
            updatedUser.setAddress(userDetails.getAddress());
            updatedUser.setCne(userDetails.getCne());
            updatedUser.setSector(userDetails.getSector());
            updatedUser.setUserName(userDetails.getUsername());
            updatedUser.setRole(userDetails.getRole());

            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                updatedUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            userRepository.save(updatedUser);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {

        Optional<Users> user = userRepository.findById(id);
        if (user.isPresent()) {
            List<Borrower> borrowers = borrowerRepository.findByUserUserId(id);
            for (Borrower borrower : borrowers) {
                borrower.setUser(null);
            }
            borrowerRepository.saveAll(borrowers);

            userRepository.delete(user.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getByUsername/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
        Optional<Users> user = userRepository.findByUserName(username);
        if (user.isPresent()) {
            Users u = user.get();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userId", u.getUserId());
            userMap.put("firstname", u.getFirstname());
            userMap.put("lastname", u.getLastname());
            userMap.put("email", u.getEmail());
            userMap.put("gsm", u.getGsm());
            userMap.put("address", u.getAddress());
            userMap.put("cne", u.getCne());
            userMap.put("sector", u.getSector());
            userMap.put("userName", u.getUsername());
            userMap.put("role", u.getRole());
            return ResponseEntity.ok(userMap);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

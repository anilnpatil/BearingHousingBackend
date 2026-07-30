package TVS_SFL.BearingHousingBackend.auth.controllers;

import TVS_SFL.BearingHousingBackend.auth.dto.ApiResponse;
import TVS_SFL.BearingHousingBackend.auth.dto.AuthRequest;
import TVS_SFL.BearingHousingBackend.auth.dto.RegisterRequest;
import TVS_SFL.BearingHousingBackend.auth.entities.User;
import TVS_SFL.BearingHousingBackend.auth.repositories.UserRepository;
import TVS_SFL.BearingHousingBackend.auth.security.JwtUtil;
import TVS_SFL.BearingHousingBackend.auth.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;    
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest req) {
        try {
            // Check if user already exists
            if (userRepo.findByUsername(req.getUsername()).isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new ApiResponse("error", "Username already exists"));
            }

            User user = User.builder()
                    .username(req.getUsername())
                    .password(encoder.encode(req.getPassword()))
                    .role(req.getRole())
                    .build();

            userRepo.save(user);

            return ResponseEntity.ok(new ApiResponse("success", "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Registration failed: " + e.getMessage()));
        }
    }

@PostMapping("/login")
public Map<String, String> login(@RequestBody AuthRequest req) {
    authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
    );

    User user = userRepo.findByUsername(req.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

    return Map.of("token", token);
}

@PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {        
        return ResponseEntity.ok("User logged out successfully!");
    }

    /**     
     * @return ResponseEntity with list of all users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of(
                            "status", "success",
                            "data", userService.getAllUsers(),
                            "count", userService.getAllUsers().size()
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to retrieve users: " + e.getMessage()));
        }
    }
    
    /**     
     * @param id The user ID to delete
     * @return ResponseEntity with success or error message
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse("error", "Invalid user ID"));
            }

            boolean deleted = userService.deleteUser(id);
            
            if (deleted) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ApiResponse("success", "User deleted successfully"));
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("error", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to delete user: " + e.getMessage()));
        }
    }

}
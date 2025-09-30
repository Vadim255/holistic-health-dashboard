package frolov.models;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;
}

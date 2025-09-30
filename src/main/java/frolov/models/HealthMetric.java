package frolov.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class HealthMetric {
    private Long id;
    private User user;
    private LocalDateTime timestamp;
}

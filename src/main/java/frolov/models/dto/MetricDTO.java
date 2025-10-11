package frolov.models.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MetricDTO {
    private Long userId;

    private MetricType type;

    private double value;

    private LocalDateTime timestamp;
}

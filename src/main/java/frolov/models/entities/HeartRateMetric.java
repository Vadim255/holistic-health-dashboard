package frolov.models.entities;

import frolov.models.entities.HealthMetric;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("HEART_RATE")
public class HeartRateMetric extends HealthMetric {
    private int beatsPerMinute;
}

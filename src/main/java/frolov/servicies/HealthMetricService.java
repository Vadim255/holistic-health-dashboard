package frolov.servicies;

import frolov.models.entities.HealthMetric;
import frolov.repositories.HealthMetricRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class HealthMetricService {
    private final HealthMetricRepository healthMetricRepository;

    public HealthMetricService(HealthMetricRepository healthMetricRepository) {
        this.healthMetricRepository = healthMetricRepository;
    }

    @Transactional
    public HealthMetric saveMetric(HealthMetric metric) {
        return healthMetricRepository.save(metric);
    }
}

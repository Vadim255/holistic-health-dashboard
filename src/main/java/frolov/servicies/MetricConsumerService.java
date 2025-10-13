package frolov.servicies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import frolov.models.dto.MetricDTO;
import frolov.models.entities.HealthMetric;
import frolov.models.entities.User;
import frolov.models.entities.WeightMetric;
import frolov.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MetricConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(MetricConsumerService.class);

    private final ObjectMapper objectMapper;
    private final HealthMetricService healthMetricService;
    private final UserRepository userRepository;



    public MetricConsumerService(ObjectMapper objectMapper,
                                 UserRepository userRepository,
                                 HealthMetricService healthMetricService) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.healthMetricService = healthMetricService;
    }

    @KafkaListener(topics = "${kafka.topic.health-metrics}", groupId = "health-metric-group")
    public void consume(@Payload String message) {
        logger.info("-> Received message from Kafka: {}", message);

        try {
            MetricDTO metricDto = objectMapper.readValue(message, MetricDTO.class);

            Optional<User> userOptional = userRepository.findById(metricDto.getUserId());
            if (userOptional.isEmpty()) {
                logger.warn("User with id {} not found. Metric will be skipped.", metricDto.getUserId());
                return;
            }
            User user = userOptional.get();

            HealthMetric healthMetric = convertDtoToEntity(metricDto, user);

            healthMetricService.saveMetric(healthMetric);
            logger.info("<- Successfully delegated saving of {} for user {}", healthMetric.getClass().getSimpleName(), user.getId());

        } catch (JsonProcessingException e) {
            logger.error("! Deserialization failed for message: {}", message, e);
        } catch (IllegalArgumentException e) {
            logger.error("! Business logic error: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("! An unexpected error occurred while processing message: {}", message, e);
        }
    }

    private HealthMetric convertDtoToEntity(MetricDTO dto, User user) {
        switch (dto.getType()) {
            case WEIGHT:
                WeightMetric weightMetric = new WeightMetric();
                weightMetric.setValueKg(dto.getValue());
                weightMetric.setUser(user);
                weightMetric.setTimestamp(dto.getTimestamp());
                return weightMetric;

            default:
                throw new IllegalArgumentException("Unknown metric type received: " + dto.getType());
        }
    }
}

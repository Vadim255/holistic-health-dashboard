package frolov.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import frolov.models.dto.MetricDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics" )
public class HealthMetricController {

    private static final Logger logger = LoggerFactory.getLogger(HealthMetricController.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicName;

    public HealthMetricController(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${kafka.topic.health-metrics}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topicName = topicName;
    }

    @PostMapping
    public ResponseEntity<Void> submitMetric(@RequestBody MetricDTO metricDto) {
        if (metricDto.getUserId() == null || metricDto.getType() == null || metricDto.getTimestamp() == null) {
            logger.warn("Received invalid metric DTO: {}", metricDto);
            return ResponseEntity.badRequest().build();
        }

        try {
            String message = objectMapper.writeValueAsString(metricDto);
            logger.info("Sending metric to Kafka topic '{}': {}", topicName, message);
            kafkaTemplate.send(topicName, String.valueOf(metricDto.getUserId()), message);
            return ResponseEntity.accepted().build();
        } catch (JsonProcessingException e) {
            logger.error("Error serializing metric DTO to JSON: {}", metricDto, e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            logger.error("Error sending message to Kafka", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

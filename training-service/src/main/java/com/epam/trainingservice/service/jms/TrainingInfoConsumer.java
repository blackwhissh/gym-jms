package com.epam.trainingservice.service.jms;

import com.epam.trainingservice.dto.TrainingInfoMessage;
import com.epam.trainingservice.service.TrainingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;


@Service
public class TrainingInfoConsumer {
    private static final Logger log = LoggerFactory.getLogger(TrainingInfoConsumer.class);
    private final TrainingService trainingService;
    private final ObjectMapper objectMapper;

    public TrainingInfoConsumer(TrainingService trainingService, ObjectMapper objectMapper) {
        this.trainingService = trainingService;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "${spring.activemq.destination}")
    public void receive(Message<TrainingInfoMessage> message) throws JsonProcessingException {
        TrainingInfoMessage request = objectMapper.readValue(message.getPayload().toString(), TrainingInfoMessage.class);
        log.info("Received message: " + request.toString());
        trainingService.saveInfo(request);
    }
}

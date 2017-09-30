package com.severett.rapid_stats_aggregator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.severett.rapid_stats_aggregator.dto.InputDTO;
import com.severett.rapid_stats_aggregator.exception.StatsParserException;
import com.severett.rapid_stats_aggregator.model.ComputerStats;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ComputerStatsParserImpl implements ComputerStatsParser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerStatsParserImpl.class);
    
    private final ObjectMapper objMapper;
    private final ValidatorFactory validatorFactory;
    
    public ComputerStatsParserImpl() {
        objMapper = new ObjectMapper();
        validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @Override
    public ComputerStats parseComputerStats(InputDTO<JSONObject> inputDTO) throws StatsParserException {
        try {
            LOGGER.debug("Parsing '{}'", inputDTO.getPayload().toString());
            ComputerStats computerStats = objMapper.readValue(inputDTO.getPayload().toString(), ComputerStats.class);
            computerStats.setComputerUuid(inputDTO.getComputerUuid());
            computerStats.setTimestamp(inputDTO.getTimestamp());
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<ComputerStats>> constraintViolations = validator.validate(computerStats);
            if (constraintViolations.isEmpty()) {
                return computerStats;
            } else {
                throw new StatsParserException(
                    constraintViolations.stream()
                            .map(cv -> String.format("%s value '%s' %s", cv.getPropertyPath(), cv.getInvalidValue(), cv.getMessage()))
                            .collect(Collectors.joining("; "))
                );
            }
        } catch (NumberFormatException | IOException e) {
            throw new StatsParserException(String.format("Bad Format Exception: %s", e.getMessage()));
        }
    }
    
}

/**
 * Copyright 2017 Severn Everett
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.severett.rapid_stats_aggregator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.severett.rapid_stats_aggregator.dto.StatsDTO;
import com.severett.rapid_stats_aggregator.exception.StatsParserException;
import com.severett.rapid_stats_aggregator.model.ComputerStats;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
    public ComputerStats parseComputerStats(StatsDTO statsDTO) throws StatsParserException {
        try {
            LOGGER.debug("Parsing for {} '{}'", statsDTO.getComputerUuid(), statsDTO.getStats().toString());
            ComputerStats computerStats = objMapper.readValue(statsDTO.getStats().toString(), ComputerStats.class);
            LOGGER.debug("Parsed stats for: {}", statsDTO.getComputerUuid());
            computerStats.setComputerUuid(statsDTO.getComputerUuid());
            computerStats.setTimestamp(statsDTO.getTimestamp());
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

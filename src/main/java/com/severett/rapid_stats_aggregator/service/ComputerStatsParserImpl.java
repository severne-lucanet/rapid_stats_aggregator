package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.exception.StatsParserException;
import com.severett.rapid_stats_aggregator.exception.UnsupportedVersionException;
import com.severett.rapid_stats_aggregator.model.ComputerStats;
import com.severett.rapid_stats_aggregator.parser.StatsJSONParserGuidelines;
import com.severett.rapid_stats_aggregator.parser.VersionOneGuidelines;
import com.severett.rapid_stats_aggregator.parser.VersionThreeGuidelines;
import com.severett.rapid_stats_aggregator.parser.VersionTwoGuidelines;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ComputerStatsParserImpl implements ComputerStatsParser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerStatsParserImpl.class);
    
    private final Map<Integer, StatsJSONParserGuidelines> guidelinesDirectory;
    
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    
    public ComputerStatsParserImpl() {
        guidelinesDirectory = new HashMap<>();
        guidelinesDirectory.put(1, new VersionOneGuidelines());
        guidelinesDirectory.put(2, new VersionTwoGuidelines());
        guidelinesDirectory.put(3, new VersionThreeGuidelines());
    }

    @Override
    public ComputerStats parseComputerStats(String computerUuid, JSONObject statsObject) throws UnsupportedVersionException, StatsParserException {
        try {
            int statsVersion = statsObject.getInt("version");
            StatsJSONParserGuidelines parserGuidelines = Optional.ofNullable(guidelinesDirectory.getOrDefault(statsVersion, null))
                    .orElseThrow(() -> new UnsupportedVersionException(statsVersion));
            ComputerStats computerStats = new ComputerStats();
            computerStats.setComputerUuid(computerUuid);
            Map<ComputerStats.StatName, BiConsumer<ComputerStats, String>> guidelines = parserGuidelines.getGuidelines();
            for (ComputerStats.StatName statName : guidelines.keySet()) {
                String statNameStr = statName.toString();
                if (statsObject.has(statNameStr)) {
                    guidelines.get(statName).accept(computerStats, statsObject.getString(statName.toString()));
                } else {
                    LOGGER.warn("Stat {} not found for stat version {} statistics", statNameStr, statsVersion);
                }
            }
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
        } catch (JSONException jsone) {
            throw new StatsParserException(jsone.getMessage());
        } catch (NumberFormatException nfe) {
            throw new StatsParserException(String.format("Bad Format Exception: %s", nfe.getMessage()));
        }
    }
    
}

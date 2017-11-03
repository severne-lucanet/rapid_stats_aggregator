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

import io.reactivex.disposables.Disposable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import com.severett.rapid_stats_aggregator.exception.StatsParserException;
import com.severett.rapid_stats_aggregator.model.ComputerStats;

@Service
public class StatisticsProcessorImpl implements StatisticsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsProcessorImpl.class);
    
    private final ComputerStatsParser computerStatsParser;
    private final Persister persister;
    
    @Autowired
    public StatisticsProcessorImpl(ComputerStatsParser computerStatsParser, Persister persister) {
        this.computerStatsParser = computerStatsParser;
        this.persister = persister;
    }

    @Override
    public void onNext(InputDTO<JSONObject> inputDTO) {
        String computerUuid = inputDTO.getComputerUuid();
        LOGGER.debug("Processing statistics for computer {}", computerUuid);
        try {
            ComputerStats computerStats = computerStatsParser.parseComputerStats(inputDTO);
            persister.saveComputerStats(computerStats);
        } catch (StatsParserException spe) {
            LOGGER.error("Statistics parsing error for computer {}: {}", computerUuid, spe.getMessage());
        }
    }

    @Override
    public void onError(Throwable e) {
        LOGGER.error("Error in StatisticsProcessorImpl: {}", e.getMessage());
    }

    @Override
    public void onComplete() {
        //No-op
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        //No-op
    }
}

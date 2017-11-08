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

import com.severett.rapid_stats_aggregator.model.ComputerStats;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("Remote")
public class RemotePersisterImpl implements Persister {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RemotePersisterImpl.class);

    @Override
    public void saveComputerStats(ComputerStats computerStats) {
        LOGGER.debug("Uploading stats for {} to S3", computerStats.getComputerUuid());
    }

    @Override
    public void saveLogFile(File logFile) throws IOException {
        LOGGER.debug("Uploading log file to Kibana server");
    }
    
}

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

import io.reactivex.Observer;
import java.io.IOException;
import java.io.InputStream;

public interface LogFileProcessor extends Observer<String> {
    
    void processLogFile(String computerUUID, Long timestamp, InputStream fileStream) throws IOException;
    
    void processOutstandingFiles() throws IOException;
    
}

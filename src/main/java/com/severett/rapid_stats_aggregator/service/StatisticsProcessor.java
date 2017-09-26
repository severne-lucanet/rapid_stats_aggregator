package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import org.json.JSONObject;
import reactor.bus.Event;
import reactor.fn.Consumer;

public interface StatisticsProcessor extends Consumer<Event<InputDTO<JSONObject>>> {
    
}

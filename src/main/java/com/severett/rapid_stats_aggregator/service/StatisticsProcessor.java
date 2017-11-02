package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import io.reactivex.Observer;
import org.json.JSONObject;

public interface StatisticsProcessor extends Observer<InputDTO<JSONObject>> {
}

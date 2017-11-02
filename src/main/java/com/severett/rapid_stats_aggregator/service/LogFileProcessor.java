package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import io.reactivex.Observer;

public interface LogFileProcessor extends Observer<InputDTO<byte[]>> {
}

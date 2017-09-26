package com.severett.rapid_stats_aggregator.service;

import com.severett.rapid_stats_aggregator.dto.InputDTO;
import reactor.bus.Event;
import reactor.fn.Consumer;

public interface LogFileProcessor extends Consumer<Event<InputDTO<byte[]>>> {
}

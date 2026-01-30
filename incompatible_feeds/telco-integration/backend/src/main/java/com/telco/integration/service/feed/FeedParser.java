package com.telco.integration.service.feed;

import com.telco.integration.service.dto.RawFeedItem;
import java.util.List;

public interface FeedParser {
    List<RawFeedItem> parse();
    String source();
}

package com.googlecloud.vertex.ai.insights.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnergyGraphDto {
	
	private Map<String, EnergyLevelDetail> timeBasedElements = new HashMap<>();

    public Map<String, EnergyLevelDetail> getTimeBasedElements() {
        return timeBasedElements;
    }

    public void setTimeBasedElements(Map<String, EnergyLevelDetail> timeBasedElements) {
        this.timeBasedElements = timeBasedElements;
    }

}

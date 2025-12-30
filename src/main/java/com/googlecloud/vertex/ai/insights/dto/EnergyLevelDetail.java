package com.googlecloud.vertex.ai.insights.dto;

public class EnergyLevelDetail {
	
	private String timeRange;
    private Integer percentEnergyLevel;

    public EnergyLevelDetail() {}

    public EnergyLevelDetail(String timeRange, Integer percentEnergyLevel) {
        this.timeRange = timeRange;
        this.percentEnergyLevel = percentEnergyLevel;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public Integer getPercentEnergyLevel() {
        return percentEnergyLevel;
    }

    public void setPercentEnergyLevel(Integer percentEnergyLevel) {
        this.percentEnergyLevel = percentEnergyLevel;
    }

}

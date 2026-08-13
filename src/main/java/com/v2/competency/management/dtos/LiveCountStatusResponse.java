package com.v2.competency.management.dtos;

public class LiveCountStatusResponse {
	private boolean withinLimit;
    private int liveCount;

    public LiveCountStatusResponse() {
    }

    public LiveCountStatusResponse(boolean withinLimit, int liveCount) {
        this.withinLimit = withinLimit;
        this.liveCount = liveCount;
    }

    public boolean isWithinLimit() {
        return withinLimit;
    }

    public void setWithinLimit(boolean withinLimit) {
        this.withinLimit = withinLimit;
    }

    public int getLiveCount() {
        return liveCount;
    }

    public void setLiveCount(int liveCount) {
        this.liveCount = liveCount;
    }
}

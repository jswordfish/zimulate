package com.voice.avatar.platform.resemble.dto;

import java.time.OffsetDateTime;

public class Item {
	private String uuid;
    private String body;
    private String voiceUUID;
    private boolean isArchived;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String audioSrc;
    private OffsetDateTime lastGeneratedAt;
    private Timestamps timestamps;

    public String getUUID() { return uuid; }
    public void setUUID(String value) { this.uuid = value; }

    public String getBody() { return body; }
    public void setBody(String value) { this.body = value; }

    public String getVoiceUUID() { return voiceUUID; }
    public void setVoiceUUID(String value) { this.voiceUUID = value; }

    public boolean getIsArchived() { return isArchived; }
    public void setIsArchived(boolean value) { this.isArchived = value; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime value) { this.createdAt = value; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime value) { this.updatedAt = value; }

    public String getAudioSrc() { return audioSrc; }
    public void setAudioSrc(String value) { this.audioSrc = value; }

    public OffsetDateTime getLastGeneratedAt() { return lastGeneratedAt; }
    public void setLastGeneratedAt(OffsetDateTime value) { this.lastGeneratedAt = value; }

    public Timestamps getTimestamps() { return timestamps; }
    public void setTimestamps(Timestamps value) { this.timestamps = value; }
}

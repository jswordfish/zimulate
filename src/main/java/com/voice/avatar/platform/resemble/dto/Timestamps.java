package com.voice.avatar.platform.resemble.dto;

public class Timestamps {
	private Object[] phonChars;
    private Object[] phonTimes;
    private String[] graphChars;
    private double[][] graphTimes;

    public Object[] getPhonChars() { return phonChars; }
    public void setPhonChars(Object[] value) { this.phonChars = value; }

    public Object[] getPhonTimes() { return phonTimes; }
    public void setPhonTimes(Object[] value) { this.phonTimes = value; }

    public String[] getGraphChars() { return graphChars; }
    public void setGraphChars(String[] value) { this.graphChars = value; }

    public double[][] getGraphTimes() { return graphTimes; }
    public void setGraphTimes(double[][] value) { this.graphTimes = value; }

}

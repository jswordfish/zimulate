package com.v2.competency.management.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:custom.properties")
public class PropertyConfig {
	
	@Value("${assessment.url}")
	String assessmentUrl;
	
	@Value("${eAssessTokenAPI}")
	String tokenApi;
	
	@Value("${eAssessGetTestLinkApi}")
	String getTestLinkApi;
	
	@Value("${eAssessCheckTestStatusForUser}")
	String checkTestStatusForUserApi;
	
	@Value("${eAssessUserTestResultApi}")
	String testResultApi;
	
	@Value("${fileserver.path}")
	String fileServerPath;
	
	@Value("${fileserver.url}")
	String fileServerBaseUrl;
	
	@Value("${videoServer.url}")
	String videoFileServerBaseUrl;
	
	@Value("${ai.test.url}")
	String aitestUrl;
	
	
	@Value("${ai.test.private.url}")
	String aitestPrivateUrl;
	
	@Value("${ai.role.play.test.url}")
	String aiRolePlayTestUrl;
	
	@Value("${ai.role.play.test.private.url}")
	String aiRolePlayTestPrivateUrl;
	
	@Value("${ai.gemini.project.id}")
	String geminiProjectId;
	
	@Value("${ai.gemini.project.location}")
	String geminiLocation;
	
	@Value("${ai.gemini.project.modelName}")
	String geminiModelName;
	
	
	@Value("${roleplay.result.url}")
	String rolePlayResultBaseUrl;;
	
	@Value("${elevenlabs.api.key}")
	String elevenLabsKey;
	
	@Value("${elevenlabs.api.base.url}")
	String elevenLabsApiUrl;
	
	@Value("${live.count.limit}")
	Integer liveCountLimit;
	
	@Value("${path.to.custom.properties}")
	String customPropertiesPath;

	public String getGeminiProjectId() {
		return geminiProjectId;
	}

	public void setGeminiProjectId(String geminiProjectId) {
		this.geminiProjectId = geminiProjectId;
	}

	public String getGeminiLocation() {
		return geminiLocation;
	}

	public void setGeminiLocation(String geminiLocation) {
		this.geminiLocation = geminiLocation;
	}

	public String getGeminiModelName() {
		return geminiModelName;
	}

	public void setGeminiModelName(String geminiModelName) {
		this.geminiModelName = geminiModelName;
	}

	public String getAssessmentUrl() {
		return assessmentUrl;
	}

	public void setAssessmentUrl(String assessmentUrl) {
		this.assessmentUrl = assessmentUrl;
	}

	public String getTokenApi() {
		return tokenApi;
	}

	public void setTokenApi(String tokenApi) {
		this.tokenApi = tokenApi;
	}

	public String getGetTestLinkApi() {
		return getTestLinkApi;
	}

	public void setGetTestLinkApi(String getTestLinkApi) {
		this.getTestLinkApi = getTestLinkApi;
	}

	public String getCheckTestStatusForUserApi() {
		return checkTestStatusForUserApi;
	}

	public void setCheckTestStatusForUserApi(String checkTestStatusForUserApi) {
		this.checkTestStatusForUserApi = checkTestStatusForUserApi;
	}

	public String getTestResultApi() {
		return testResultApi;
	}

	public void setTestResultApi(String testResultApi) {
		this.testResultApi = testResultApi;
	}

	public String getFileServerPath() {
		return fileServerPath;
	}

	public void setFileServerPath(String fileServerPath) {
		this.fileServerPath = fileServerPath;
	}

	public String getFileServerBaseUrl() {
		return fileServerBaseUrl;
	}

	public void setFileServerBaseUrl(String fileServerBaseUrl) {
		this.fileServerBaseUrl = fileServerBaseUrl;
	}

	public String getAitestUrl() {
		return aitestUrl;
	}

	public void setAitestUrl(String aitestUrl) {
		this.aitestUrl = aitestUrl;
	}

	public String getAitestPrivateUrl() {
		return aitestPrivateUrl;
	}

	public void setAitestPrivateUrl(String aitestPrivateUrl) {
		this.aitestPrivateUrl = aitestPrivateUrl;
	}

	public String getAiRolePlayTestUrl() {
		return aiRolePlayTestUrl;
	}

	public void setAiRolePlayTestUrl(String aiRolePlayTestUrl) {
		this.aiRolePlayTestUrl = aiRolePlayTestUrl;
	}

	public String getAiRolePlayTestPrivateUrl() {
		return aiRolePlayTestPrivateUrl;
	}

	public void setAiRolePlayTestPrivateUrl(String aiRolePlayTestPrivateUrl) {
		this.aiRolePlayTestPrivateUrl = aiRolePlayTestPrivateUrl;
	}

	public String getVideoFileServerBaseUrl() {
		return videoFileServerBaseUrl;
	}

	public void setVideoFileServerBaseUrl(String videoFileServerBaseUrl) {
		this.videoFileServerBaseUrl = videoFileServerBaseUrl;
	}

	public String getRolePlayResultBaseUrl() {
		return rolePlayResultBaseUrl;
	}

	public void setRolePlayResultBaseUrl(String rolePlayResultBaseUrl) {
		this.rolePlayResultBaseUrl = rolePlayResultBaseUrl;
	}

	public String getElevenLabsKey() {
		return elevenLabsKey;
	}

	public void setElevenLabsKey(String elevenLabsKey) {
		this.elevenLabsKey = elevenLabsKey;
	}

	public String getElevenLabsApiUrl() {
		return elevenLabsApiUrl;
	}

	public void setElevenLabsApiUrl(String elevenLabsApiUrl) {
		this.elevenLabsApiUrl = elevenLabsApiUrl;
	}

	public Integer getLiveCountLimit() {
		return liveCountLimit;
	}

	public void setLiveCountLimit(Integer liveCountLimit) {
		this.liveCountLimit = liveCountLimit;
	}

	public String getCustomPropertiesPath() {
		return customPropertiesPath;
	}

	public void setCustomPropertiesPath(String customPropertiesPath) {
		this.customPropertiesPath = customPropertiesPath;
	}
	
	
	

}

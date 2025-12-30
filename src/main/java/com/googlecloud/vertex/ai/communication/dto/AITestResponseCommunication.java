package com.googlecloud.vertex.ai.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AITestResponseCommunication {
	
	String transcript;
	
	ExpectedResponseCommunication response;

}

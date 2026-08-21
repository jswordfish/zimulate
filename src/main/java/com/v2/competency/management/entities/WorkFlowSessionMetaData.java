package com.v2.competency.management.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowSessionMetaData extends Base{
	
	private Long rolePlaySessionId;
	
	private Long elevenLabsSessionId;
	
	private String rolePlayTestName;
	
	private Integer attempt;
	
	private String email;
	
	private LocalDateTime startTime;
	
	private LocalDateTime endTime;
	
	private Integer durationInMinutes;
	
	private Long tokenUsed;

}

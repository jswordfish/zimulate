package com.v2.competency.management.entities;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FailedAttemptSession extends Base {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "session_id")
//    private VFRolePlayTestSession session;
	
	private Long sessionId;

    private Integer geminiAttempt;

    private Boolean success;
}
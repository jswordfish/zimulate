package com.v2.competency.management.dtos;

import java.util.List;

import com.v2.competency.management.entities.VFRolePlayTestSession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePlaySessionContainerDto {
	
	List<VFRolePlayTestSession> success;
	
	List<VFRolePlayTestSession> failed;

}

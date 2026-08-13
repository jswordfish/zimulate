package com.v2.competency.management.entities;

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
public class ZimulateWorkflow extends Base{
	
	String name;
	
	String objective;
	
	String industry;
	
	String image;
	
	Boolean complete;
	
	Boolean navigationBack;
}

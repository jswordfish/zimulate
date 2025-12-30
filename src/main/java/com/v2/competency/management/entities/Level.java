package com.v2.competency.management.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
public class Level extends Base{
	
	String levelName;
	
	@ManyToOne
	@JoinColumn(name = "parent")
	Level parent;
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "parent",cascade = CascadeType.ALL)
	List<Level> childLevels;
	
	
}

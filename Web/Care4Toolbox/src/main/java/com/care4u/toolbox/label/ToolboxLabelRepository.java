package com.care4u.toolbox.label;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.toolbox.Toolbox;

public interface ToolboxLabelRepository extends JpaRepository<ToolboxLabel, Long> {
	
	List<ToolboxLabel> findAllByToolboxOrderByLocationAsc(Toolbox toolbox);
		
	ToolboxLabel findByToolboxAndLocation(Toolbox toolbox, String location);
	
}
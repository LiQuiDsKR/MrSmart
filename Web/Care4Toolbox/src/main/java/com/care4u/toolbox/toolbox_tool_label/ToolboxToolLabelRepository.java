package com.care4u.toolbox.toolbox_tool_label;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.toolbox.Toolbox;

public interface ToolboxToolLabelRepository extends JpaRepository<ToolboxToolLabel, Long> {
	
	List<ToolboxToolLabel> findAllByToolboxOrderByLocationAsc(Toolbox toolbox);
		
	ToolboxToolLabel findByToolboxAndLocation(Toolbox toolbox, String location);
	
	ToolboxToolLabel findByToolIdAndToolboxId(long toolId, long toolboxId);
}
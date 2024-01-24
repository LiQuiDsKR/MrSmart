package com.care4u.toolbox.group.working_tool;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingToolRepository extends JpaRepository<WorkingTool, Long> {
	
	List<WorkingTool> findAllByWorkingToolboxId(long workingToolboxId);
	
	WorkingTool findByWorkingToolboxIdAndToolId(long workingToolboxId, long toolId);
}
package com.care4u.toolbox.group.working_toolbox;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingToolboxRepository extends JpaRepository<WorkingToolbox, Long> {
	
	List<WorkingToolbox> findAllByOrderByNameAsc();
		
	WorkingToolbox findByName(String name);
	
}
package com.care4u.toolbox.tool;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolRepository extends JpaRepository<Tool, Long> {
	
	List<Tool> findAllByOrderByNameAsc();
	
	List<Tool> findAllBySubGroupIdOrderByNameAsc(long subGroupId);
		
	Tool findByCode(String code);
}
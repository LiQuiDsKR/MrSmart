package com.care4u.toolbox.tool;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolRepository extends JpaRepository<Tool, Long> {
	
	List<Tool> findAllByOrderByNameAsc();
	
	Page<Tool> findAllByOrderByNameAsc(Pageable pageable);
	
	Page<Tool> findAllByNameLikeOrderByNameAsc(Pageable pageable, String name);
	
	Page<Tool> findAllBySubGroupIdOrderByNameAsc(Pageable pageable, long subGroupId);
	
	List<Tool> findAllBySubGroupIdOrderByNameAsc(long subGroupId);
		
	Tool findByCode(String code);
	
}
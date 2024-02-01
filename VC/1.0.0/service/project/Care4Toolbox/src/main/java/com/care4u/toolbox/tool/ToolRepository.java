package com.care4u.toolbox.tool;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolRepository extends JpaRepository<Tool, Long> ,ToolRepositoryCustom{
	
	List<Tool> findAllByOrderByNameAsc();
	
	Page<Tool> findAllByOrderByNameAsc(Pageable pageable);
	
	Page<Tool> findAllByNameLikeOrderByNameAsc(Pageable pageable, String name);
	
	Page<Tool> findAllBySubGroupIdOrderByNameAsc(Pageable pageable, long subGroupId);
	
	Page<Tool> findAllBySubGroupIdAndNameLikeOrderByNameAsc(Pageable pageable, long subGroupId, String name);
	
	List<Tool> findAllBySubGroupIdOrderByNameAsc(long subGroupId);
		
	Tool findByCode(String code);

	//Page<Tool> findByNameContaining(Pageable pageable, String name);

	Page<Tool> findByNameContainingAndSubGroupIdIn(Pageable pageable, String name, List<Long> subGroupId);
	
}
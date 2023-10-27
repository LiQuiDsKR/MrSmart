package com.care4u.toolbox;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolboxRepository extends JpaRepository<Toolbox, Long> {
	
	List<Toolbox> findAllByOrderByNameAsc();
	
	Toolbox findByName(String name);
	
}
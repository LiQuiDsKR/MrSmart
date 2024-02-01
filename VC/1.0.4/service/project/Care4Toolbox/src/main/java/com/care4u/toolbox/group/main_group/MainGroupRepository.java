package com.care4u.toolbox.group.main_group;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MainGroupRepository extends JpaRepository<MainGroup, Long> {
	
	List<MainGroup> findAllByOrderByNameAsc();
	MainGroup findByName(String name);
	
}
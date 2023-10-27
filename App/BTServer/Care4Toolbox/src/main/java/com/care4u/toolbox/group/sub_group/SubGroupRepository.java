package com.care4u.toolbox.group.sub_group;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.toolbox.group.main_group.MainGroup;

public interface SubGroupRepository extends JpaRepository<SubGroup, Long> {
	
	List<SubGroup> findAllByOrderByNameAsc();
	
	List<SubGroup> findAllByMainGroupOrderByNameAsc(MainGroup mainGroup);
		
	SubGroup findByMainGroupIdAndName(long mainGroupId, String name);
}
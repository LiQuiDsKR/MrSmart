package com.care4u.toolbox.tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
	
	List<Tag> findAllByToolboxId(long toolbox);
		
	Tag findByMacaddress(String macaddress);
	
}
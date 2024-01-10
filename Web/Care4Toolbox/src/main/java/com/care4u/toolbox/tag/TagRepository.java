package com.care4u.toolbox.tag;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
	
	List<Tag> findAllByToolboxId(long toolboxId);
	
	Page<Tag> findAllByToolboxId(long toolboxId, Pageable pageable);
	
	List<Tag> findAllByRentalToolId(long rentalToolId);
		
	Tag findByMacaddress(String macaddress);
	
	List<Tag> findAllByToolIdAndToolboxId(long toolId, long toolboxId);
	
	long countByToolIdAndToolboxId(long toolId, long toolboxId);
	
	List<Tag> findByTagGroup(String tagGroup);
	
	long countByToolboxId(long toolboxId);
}
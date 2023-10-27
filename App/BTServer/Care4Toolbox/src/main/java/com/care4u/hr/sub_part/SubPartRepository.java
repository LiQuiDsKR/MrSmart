package com.care4u.hr.sub_part;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.hr.main_part.MainPart;

public interface SubPartRepository extends JpaRepository<SubPart, Long> {
	
	public List<SubPart> findAllByOrderByNameAsc();
	
	public List<SubPart> findAllByMainPartOrderByNameAsc(MainPart mainPart);
		
	public SubPart findByMainPartIdAndName(long mainPartId, String name);
}
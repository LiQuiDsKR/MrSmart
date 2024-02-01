package com.care4u.hr.part;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.hr.sub_part.SubPart;

public interface PartRepository extends JpaRepository<Part, Long> {
	
	public List<Part> findAllByOrderByNameAsc();
	
	public List<Part> findAllBySubPartOrderByNameAsc(SubPart subPart);
		
	public Part findBySubPartIdAndName(long subPartId, String name);

	public List<Part> findByNameContaining(String name);
}
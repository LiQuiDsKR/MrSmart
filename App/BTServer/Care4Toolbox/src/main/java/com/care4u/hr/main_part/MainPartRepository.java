package com.care4u.hr.main_part;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MainPartRepository extends JpaRepository<MainPart, Long> {
	
	public List<MainPart> findAllByOrderByNameAsc();
	public MainPart findByName(String name);
	
}
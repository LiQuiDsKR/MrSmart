package com.care4u.hr.membership;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.constant.Role;
import com.care4u.hr.part.Part;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

	public List<Membership> findAllByRoleOrderByNameAsc(Role role);
	
	public List<Membership> findAllByPartOrderByNameAsc(Part part);
	
	public Membership findByCode(String code);

}
package com.care4u.hr.membership;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.care4u.constant.Role;
import com.care4u.hr.part.Part;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> ,MembershipRepositoryCustom
{

	public List<Membership> findAllByRoleOrderByNameAsc(Role role);
	
	public List<Membership> findAllByPartOrderByNameAsc(Part part);

	public List<Membership> findAll();
	
	public Membership findByCode(String code);
	
	public Page<Membership> findAll(Pageable pageable);
	
	//public Page<Membership> findByNameContaining(Pageable pageable, String name);

}
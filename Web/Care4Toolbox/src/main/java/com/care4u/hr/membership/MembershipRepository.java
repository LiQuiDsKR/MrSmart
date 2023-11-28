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
public interface MembershipRepository extends JpaRepository<Membership, Long>
/**
 * 2023-10-25 박경수
 * 추가 extend : QuerydslPredicateExecutor<Membership>, MembershipRepositoryCustom
 * search & paging 기능 테스트를 위해 추가했습니다
 */
		//,QuerydslPredicateExecutor<Membership>, MembershipRepositoryCustom
{

	public List<Membership> findAllByRoleOrderByNameAsc(Role role);
	
	public List<Membership> findAllByPartOrderByNameAsc(Part part);
		
	public Membership findByCode(String code);
	
	public Page<Membership> findAll(Pageable pageable);
	
	public Page<Membership> findByNameContaining(Pageable pageable, String name);

}
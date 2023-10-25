/**
 * 2023-10-25 박경수
 * search & paging 기능 테스트를 위해 추가했습니다
 */

package com.care4u.hr.membership;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MembershipRepositoryCustom {

	Page<Membership> getMembershipPage(MembershipSearchDto membershipSearchDto, Pageable pageable);
	
}

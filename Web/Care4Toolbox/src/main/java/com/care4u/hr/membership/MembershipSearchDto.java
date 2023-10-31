/**
 * 2023-10-25 박경수
 * search & paging 기능 테스트를 위해 추가했습니다
 */

package com.care4u.hr.membership;

import java.util.List;

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.part.PartDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class MembershipSearchDto {
	
	private List<Long> ids;
	
	private Role searchRole;
	
	private EmploymentState searchEmploymentStatus;
	
    private String searchBy; // id(?대신에 차라리 PART시리즈로 만드는 편이 낫지 않냐?) / name / code
	
	private String searchQuery="";
	
}

package com.care4u.hr.membership;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.part.PartDto;

@Getter @Setter
@ToString
public class MembershipDto {

	private Long id;
	
	private String name;
	
	private String code;
	
	private String password;
	
	private PartDto partDto;
	
	private Role role;
	
	private EmploymentState employmentStatus;

	@Builder
	public MembershipDto(long id, String name, String code, String password, PartDto partDto, Role role, EmploymentState employmentStatus) {
		this.id = id;
		this.name = name;
		this.code = code;
		this.password = password;
		this.partDto = partDto;
		this.role = role;
		this.employmentStatus = employmentStatus;
	}
	
	public MembershipDto(Membership membership) {
		this.id = membership.getId();
		this.name = membership.getName();
		this.code = membership.getCode();
		this.password = membership.getPassword();
		this.partDto = new PartDto(membership.getPart());
		this.role = membership.getRole();
		this.employmentStatus = membership.getEmploymentStatus();
	}
}
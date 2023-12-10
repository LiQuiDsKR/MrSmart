package com.care4u.hr.membership;

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.entity.BaseEntity;
import com.care4u.hr.part.Part;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="membership")
public class Membership extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@NotNull
    private String name;

    @NotNull
    @Column(unique = true)
    private String code;

    @NotNull
    private String password;

    @NotNull
	@ManyToOne(fetch = FetchType.LAZY)
    private Part part;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Enumerated(EnumType.STRING)
    private EmploymentState employmentStatus;
    
    public Membership(String code) {
    	this.code = code;
    }

    @Builder
    public Membership(MembershipFormDto memberFormDto){
        this.name = memberFormDto.getName();
        this.code = memberFormDto.getCode();
        this.password = memberFormDto.getPassword();
        this.role = Role.USER;
    }
    
    public void update(Part part, MembershipDto membershipDto) {
		this.name = membershipDto.getName();
		this.password = membershipDto.getPassword();
		this.part = part;
		this.role = membershipDto.getRole();
		this.employmentStatus = membershipDto.getEmploymentStatus();
	}
    
    public void updatePassword(String password) {
    	this.password=password;
    }

}

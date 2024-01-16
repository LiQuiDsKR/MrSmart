package com.care4u.hr.part;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;
import com.care4u.hr.sub_part.SubPart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor

@Entity
@Table(name="part")
public class Part extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NotNull
	private String name;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private SubPart subPart;
	
	public void update(SubPart subPart, PartDto partDto) {
		this.name = partDto.getName();
		this.subPart = subPart;
	}
}
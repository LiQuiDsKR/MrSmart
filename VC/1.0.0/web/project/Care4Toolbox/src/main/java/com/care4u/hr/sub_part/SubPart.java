package com.care4u.hr.sub_part;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.care4u.entity.BaseEntity;
import com.care4u.hr.main_part.MainPart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor

@Entity
@Table(name="sub_part")
public class SubPart extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NotNull
	private String name;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private MainPart mainPart;
	
	private String latitude;
	
	private String longitude;
	
	private String mapScale;
		
	public void update(MainPart mainPart, SubPartDto subGroupDto) {
		this.name = subGroupDto.getName();
		this.latitude = subGroupDto.getLatitude();
		this.longitude = subGroupDto.getLongitude();
		this.mapScale = subGroupDto.getMapScale();
		this.mainPart = mainPart;
	}
}
package com.care4u.hr.main_part;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor

@Entity
@Table(name="main_part")
public class MainPart extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@Column(unique = true)
	private String name;
	
	private String latitude;
	
	private String longitude;
	
	private String mapScale;
	
	public MainPart(String name) {
		this.name = name;
	}
	
	public void update(MainPartDto mainGroupDto) {
		this.name = mainGroupDto.getName();
		this.latitude = mainGroupDto.getLatitude();
		this.longitude = mainGroupDto.getLongitude();
		this.mapScale = mainGroupDto.getMapScale();
	}
}
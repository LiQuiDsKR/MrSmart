package com.care4u.hr.sub_part;

import com.care4u.hr.main_part.MainPartDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SubPartDto {
	
	private long id;
	
	private String name;
	
	private String latitude;
	
	private String longitude;
	
	private String mapScale;
	
	private MainPartDto mainPartDto;
	
	@Builder
	public SubPartDto(long id, String name, String latitude, String longitude, String mapScale, MainPartDto mainPartDto) {
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mapScale = mapScale;
		this.mainPartDto = mainPartDto;
	}
	
	public SubPartDto(SubPart subPart) {
		this.id = subPart.getId();
		this.name = subPart.getName();
		this.latitude = subPart.getLatitude();
		this.longitude = subPart.getLongitude();
		this.mapScale = subPart.getMapScale();
		this.mainPartDto = new MainPartDto(subPart.getMainPart());
	}
	
}

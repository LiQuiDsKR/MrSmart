package com.care4u.hr.main_part;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

public class MainPartDto {
	
	private long id;
	
	private String name;
	
	private String latitude;
	
	private String longitude;
	
	private String mapScale;
	
	@Builder
	public MainPartDto(long id, String name, String latitude, String longitude, String mapScale) {
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mapScale = mapScale;
	}
	
	public MainPartDto(MainPart mainPart) {
		this.id = mainPart.getId();
		this.name = mainPart.getName();
		this.latitude = mainPart.getLatitude();
		this.longitude = mainPart.getLongitude();
		this.mapScale = mainPart.getMapScale();
	}
		
}
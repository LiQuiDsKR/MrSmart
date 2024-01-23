package com.care4u.vehicle.vehicle_tracking;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.care4u.constant.VehicleState;
import com.care4u.toolbox.group.main_group.MainGroupDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VehicleTrackingDto {

	private long id;

	private String name;
	
	private String group;
	
	@Enumerated(EnumType.STRING)
    private VehicleState vehicleStatus;
	
	@Builder
	public VehicleTrackingDto(long id, String name, String group, VehicleState vehicleStatus) {
		this.id=id;
		this.name = name;
		this.group = group;
		this.vehicleStatus=vehicleStatus;
	}

	public VehicleTrackingDto(VehicleTracking vehicleTracking) {
		this.id=vehicleTracking.getId();
		this.name=vehicleTracking.getName();
		this.group=vehicleTracking.getGroup();
		this.vehicleStatus=vehicleTracking.getVehicleStatus();
	}
	
}

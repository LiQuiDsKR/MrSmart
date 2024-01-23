package com.care4u.vehicle.vehicle_tracking;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.constant.Role;
import com.care4u.constant.VehicleState;
import com.care4u.entity.BaseEntity;
import com.care4u.toolbox.group.main_group.MainGroup;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString

@Entity
@Table(name="vehicle_tracking")
public class VehicleTracking extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NotNull
	private String name;
	
	@NotNull
	@Column(name="`group`")
	private String group;
	
	@Enumerated(EnumType.STRING)
    private VehicleState vehicleStatus;
	
	@Builder
	public VehicleTracking(String name, String group, VehicleState vehicleStatus) {
		this.name = name;
		this.group = group;
		this.vehicleStatus=vehicleStatus;
	}

	public void update(VehicleTrackingDto vehicleTrackingDto) {
		this.name = vehicleTrackingDto.getName();
		this.group = vehicleTrackingDto.getGroup();
		this.vehicleStatus=vehicleTrackingDto.getVehicleStatus();
	}
}
package com.care4u.vehicle.vehicle_tracking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.toolbox.group.main_group.MainGroup;

public interface VehicleTrackingRepository extends JpaRepository<VehicleTracking, Long> {
	
	List<VehicleTracking> findAllByOrderByGroupAsc();
	
	List<VehicleTracking> findByGroup(String group);

	//List<VehicleTracking> findAllByOrderByNameAscAndOrderByGroupAsc();

	//List<VehicleTracking> findAllByOrderByGroupAscAndOrderByNameAsc();

	List<String> findDistinctBBy();
}
package com.care4u.vehicle.vehicle_tracking;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care4u.toolbox.group.main_group.MainGroupDto;
import com.care4u.toolbox.group.main_group.MainGroupService;
import com.google.gson.Gson;

@RestController
public class VehicleTrackingRestController {

	private static final Logger logger = Logger.getLogger(VehicleTrackingRestController.class);

	@Autowired
	private VehicleTrackingService vehicleTrackingService;

	@GetMapping("/vehicle_tracking/get")
	public List<VehicleTrackingDto> getvehicleTracking() {
		List<VehicleTrackingDto> vehicleTrackingList = vehicleTrackingService.list();
		return vehicleTrackingList;
	}
	
	@GetMapping("/vehicle_tracking/get_group")
	public List<String> getVehicleTrackingGroup(){
		List<String> groupList = vehicleTrackingService.groupList();
		return groupList;
	}
	
	@PostMapping("/vehicle_tracking/set")
	public ResponseEntity<String> setVehicleTrackingState(@RequestBody VehicleTrackingDto vehicleTrackingDto, BindingResult bindingResult){
    	if (bindingResult.hasErrors()) {
    		List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
			return ResponseEntity.badRequest().body(String.join(" / ", errors));
    	}
    	logger.info(vehicleTrackingDto);
		VehicleTrackingDto resultDto = vehicleTrackingService.update(vehicleTrackingDto);
		logger.info(resultDto);
		return ResponseEntity.ok("good");
	}
}
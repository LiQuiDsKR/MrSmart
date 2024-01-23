package com.care4u.vehicle.vehicle_tracking;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.hr.sub_part.SubPartService;
import com.care4u.toolbox.group.main_group.MainGroup;
import com.care4u.toolbox.group.main_group.MainGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleTrackingService {

	private final Logger logger = LoggerFactory.getLogger(SubPartService.class);
	
	private final VehicleTrackingRepository repository;
	
	@Transactional(readOnly = true)
	public VehicleTrackingDto get(long id){
		Optional<VehicleTracking> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new VehicleTrackingDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public List<VehicleTrackingDto> list(){
		List<VehicleTracking> list = repository.findAllByOrderByGroupAsc();
		return getDtoList(list);
	}
	
	@Transactional(readOnly = true)
	public List<VehicleTrackingDto> listByGroup(String group){
		List<VehicleTracking> list  = repository.findByGroup(group);
		return getDtoList(list);
	}
	
	/*
	public VehicleTrackingDto addNew(long mainGroupId, VehicleTrackingDto subGroupDto) throws IllegalStateException {
		VehicleTracking findItem = repository.(mainGroupId, subGroupDto.getName());
		if(findItem != null){
			logger.error("이미 등옥된 이름입니다. : " + subGroupDto.getName());
			throw new IllegalStateException("이미 등옥된 이름입니다.");
		}
		return update(mainGroupId, subGroupDto);
	}
	
	public VehicleTrackingDto update(long mainGroupId, VehicleTrackingDto subGroupDto) throws IllegalStateException {
		Optional<MainGroup> mainGroup = mainGroupRepository.findById(mainGroupId);
		if (mainGroup.isEmpty()) {
			logger.error("등록되지 않은 대분류 id입니다. : " + mainGroupId);
			throw new IllegalStateException("등록되지 않은 대분류 코드입니다.");
		}
		
		VehicleTracking subGroup = repository.findByMainGroupIdAndName(mainGroupId, subGroupDto.getName());
		if (subGroup == null) {
			subGroup = new VehicleTracking();
		}
		subGroup.update(subGroupDto, mainGroup.get());
		
		return new VehicleTrackingDto(repository.save(subGroup));
	}
	
	public VehicleTrackingDto update(VehicleTrackingDto subGroupDto) throws IllegalStateException {
		Optional<MainGroup> mainGroup = mainGroupRepository.findById(subGroupDto.getMainGroupDto().getId());
		if (mainGroup.isEmpty()) {
			logger.error("등록되지 않은 대분류 id입니다. : " + subGroupDto.getMainGroupDto().getId());
			throw new IllegalStateException("등록되지 않은 대분류 코드입니다.");
		}
		
		VehicleTracking subGroup = repository.findById(subGroupDto.getId()).get();
		if (subGroup == null) {
			subGroup = new VehicleTracking();
		}
		subGroup.update(subGroupDto, mainGroup.get());
		
		return new VehicleTrackingDto(repository.save(subGroup));
	}
	*/
	
	private List<VehicleTrackingDto> getDtoList(List<VehicleTracking> list){
		List<VehicleTrackingDto> dtoList = new ArrayList<VehicleTrackingDto>();
		for (VehicleTracking item : list) {
			dtoList.add(new VehicleTrackingDto(item));
		}
		return dtoList;
	}

	public List<String> groupList() {
		List<String> list = repository.findDistinctBBy();
		return list;
	}

	public VehicleTrackingDto update(VehicleTrackingDto vehicleTrackingDto) {
		Optional<VehicleTracking> vehicleTrackingOptional = repository.findById(vehicleTrackingDto.getId());
		if (vehicleTrackingOptional.isEmpty()) {
			logger.error("등록되지 않은 id입니다. : " +vehicleTrackingDto.getId());
			throw new IllegalStateException("등록되지 않은 id입니다.");
		}
		
		VehicleTracking vehicleTracking = vehicleTrackingOptional.get(); 
		vehicleTracking.update(vehicleTrackingDto);
		
		return new VehicleTrackingDto(repository.save(vehicleTracking));
	}

}

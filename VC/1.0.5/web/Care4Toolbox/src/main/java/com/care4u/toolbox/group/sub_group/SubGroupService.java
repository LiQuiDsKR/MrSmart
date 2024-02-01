package com.care4u.toolbox.group.sub_group;

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
public class SubGroupService {

	private final Logger logger = LoggerFactory.getLogger(SubPartService.class);
	
	private final SubGroupRepository repository;
	private final MainGroupRepository mainGroupRepository;
	
	@Transactional(readOnly = true)
	public SubGroupDto get(long id){
		Optional<SubGroup> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new SubGroupDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public SubGroupDto get(long mainGroupId, String name){
		SubGroup item = repository.findByMainGroupIdAndName(mainGroupId, name);
		if (item == null) {
			return null;
		}
		
		return new SubGroupDto(item);
	}
	
	@Transactional(readOnly = true)
	public List<SubGroupDto> list(){
		List<SubGroup> list = repository.findAllByOrderByNameAsc();
		return getDtoList(list);
	}
	
	@Transactional(readOnly = true)
	public List<SubGroupDto> listByMainGroupId(long mainGroupId){
		List<SubGroupDto> list = new ArrayList<SubGroupDto>();
		Optional<MainGroup> mainGroup = mainGroupRepository.findById(mainGroupId);
		if (mainGroup.isEmpty()) {
			logger.error("Invalid mainGroupId : " + mainGroupId);
			return list;
		}
		List<SubGroup> subGroupList = repository.findAllByMainGroupOrderByNameAsc(mainGroup.get());
		return getDtoList(subGroupList);
	}
	
	public SubGroupDto addNew(long mainGroupId, SubGroupDto subGroupDto) throws IllegalStateException {
		SubGroup findItem = repository.findByMainGroupIdAndName(mainGroupId, subGroupDto.getName());
		if(findItem != null){
			logger.error("이미 등옥된 이름입니다. : " + subGroupDto.getName());
			throw new IllegalStateException("이미 등옥된 이름입니다.");
		}
		return update(mainGroupId, subGroupDto);
	}
	
	public SubGroupDto update(long mainGroupId, SubGroupDto subGroupDto) throws IllegalStateException {
		Optional<MainGroup> mainGroup = mainGroupRepository.findById(mainGroupId);
		if (mainGroup.isEmpty()) {
			logger.error("등록되지 않은 대분류 id입니다. : " + mainGroupId);
			throw new IllegalStateException("등록되지 않은 대분류 코드입니다.");
		}
		
		SubGroup subGroup = repository.findByMainGroupIdAndName(mainGroupId, subGroupDto.getName());
		if (subGroup == null) {
			subGroup = new SubGroup();
		}
		subGroup.update(subGroupDto, mainGroup.get());
		
		return new SubGroupDto(repository.save(subGroup));
	}
	
	public SubGroupDto update(SubGroupDto subGroupDto) throws IllegalStateException {
		Optional<MainGroup> mainGroup = mainGroupRepository.findById(subGroupDto.getMainGroupDto().getId());
		if (mainGroup.isEmpty()) {
			logger.error("등록되지 않은 대분류 id입니다. : " + subGroupDto.getMainGroupDto().getId());
			throw new IllegalStateException("등록되지 않은 대분류 코드입니다.");
		}
		
		SubGroup subGroup = repository.findById(subGroupDto.getId()).get();
		if (subGroup == null) {
			subGroup = new SubGroup();
		}
		subGroup.update(subGroupDto, mainGroup.get());
		
		return new SubGroupDto(repository.save(subGroup));
	}
	
	private List<SubGroupDto> getDtoList(List<SubGroup> list){
		List<SubGroupDto> dtoList = new ArrayList<SubGroupDto>();
		for (SubGroup item : list) {
			dtoList.add(new SubGroupDto(item));
			
		}
		return dtoList;
	}

}

package com.care4u.toolbox.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.hr.sub_part.SubPartService;
import com.care4u.toolbox.group.sub_group.SubGroup;
import com.care4u.toolbox.group.sub_group.SubGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ToolService {

	private final Logger logger = LoggerFactory.getLogger(SubPartService.class);
	
	private final ToolRepository repository;
	private final SubGroupRepository subGroupRepository;
	
	@Transactional(readOnly = true)
	public ToolDto get(long id){
		Optional<Tool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new ToolDto(item.get());
	}	
	
	@Transactional(readOnly = true)
	public ToolDto getByCode(String code){
		Tool item = repository.findByCode(code);
		if (item == null) {
			logger.error("Invalid code : " + code);
			return null;
		}
		
		return new ToolDto(item);
	}
	
	@Transactional(readOnly = true)
	public List<ToolDto> list(){
		List<Tool> list = repository.findAllByOrderByNameAsc();
		return getDtoList(list);
	}
	
	@Transactional(readOnly = true)
	public List<ToolDto> listBySubGroupId(long subGroupId){
		List<Tool> list = repository.findAllBySubGroupIdOrderByNameAsc(subGroupId);
		return getDtoList(list);
	}
	
	public ToolDto addNew(ToolDto toolDto) throws IllegalStateException {
		Tool findItem = repository.findByCode(toolDto.getCode());
		if(findItem != null){
			logger.error("이미 등록된 코드입니다. : " + toolDto.getCode());
			throw new IllegalStateException("이미 등록된 코드입니다.");
		}
		return update(toolDto.getSubGroupDto().getId(), toolDto);
	}
	
	public ToolDto update(long subGroupId, ToolDto toolDto) throws IllegalStateException {
		Optional<SubGroup> subGroup = subGroupRepository.findById(subGroupId);
		if (subGroup.isEmpty()) {
			throw new IllegalStateException("등록되지 않은 중분류 코드입니다.");
		}
		
		Tool tool = repository.findByCode(toolDto.getCode());
		if (tool == null) {
			tool = new Tool(toolDto.getCode());
		}
		tool.update(toolDto, subGroup.get());
		
		return new ToolDto(repository.save(tool));
	}
	
	private List<ToolDto> getDtoList(List<Tool> list){
		List<ToolDto> dtoList = new ArrayList<ToolDto>();
		for (Tool item : list) {
			dtoList.add(new ToolDto(item));
			
		}
		return dtoList;
	}

	@Transactional(readOnly = true)
	public Page<ToolDto> getToolPageByName(Pageable pageable, String name){
		Page<Tool> membershipPage = repository.findByNameContaining(pageable, name);
		return membershipPage.map(ToolDto::new);
	}
	
	@Transactional(readOnly = true)
	public Page<ToolDto> getToolPage(Pageable pageable){
		Page<Tool> toolPage = repository.findAllByOrderByNameAsc(pageable);
		logger.info("tool total page : " + toolPage.getTotalPages() + ", current page : " + toolPage.getNumber());
		return toolPage.map(ToolDto::new);
	}
}

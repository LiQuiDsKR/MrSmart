package com.care4u.toolbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.hr.main_part.MainPart;
import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ToolboxService {

	private final Logger logger = LoggerFactory.getLogger(ToolboxService.class);
	
	private final ToolboxRepository repository;
	private final MembershipRepository membershiprepository;

	@Transactional(readOnly = true)
	public ToolboxDto get(long id){
		Optional<Toolbox> item = repository.findById(id);
		if (item.isEmpty()) {
			logger.error("Invalid id : " + id);
			return null;
		}
		return new ToolboxDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public ToolboxDto get(String name){
		Toolbox item = repository.findByName(name);
		if (item == null) {
			logger.error("Invalid name : " + name);
			return null;
		}
		return new ToolboxDto(item);
	}
	
	@Transactional(readOnly = true)
	public List<ToolboxDto> list(){
		List<Toolbox> list = repository.findAllByOrderByNameAsc();
		
		List<ToolboxDto> dtoList = new ArrayList<ToolboxDto>();
		for (Toolbox item : list) {
			dtoList.add(new ToolboxDto(item));
			
		}
		return dtoList;
	}
	
	public ToolboxDto addNew(ToolboxDto toolboxDto) throws IllegalStateException {
		Toolbox findItem = repository.findByName(toolboxDto.getName());
		if(findItem != null){
			logger.error("이미 등옥된 이름입니다. : " + toolboxDto.getName());
			throw new IllegalStateException("이미 등옥된 이름입니다.");
		}
		return update(toolboxDto);
	}
	
	public ToolboxDto update(ToolboxDto toolboxDto) {
		Membership manager = membershiprepository.findByCode(toolboxDto.getManagerDto().getCode());
		if (manager == null) {
			logger.error("Invalid ManagerDto().getCode() : " + toolboxDto.getManagerDto().getCode());
			return null;
		}
		
		Toolbox toolbox = repository.findByName(toolboxDto.getName());
		if (toolbox == null) {
			toolbox = new Toolbox();
		}
		toolbox.update(toolboxDto.getName(), manager, toolboxDto.isSystemOperability());
		
		return new ToolboxDto(repository.save(toolbox));
	}
}

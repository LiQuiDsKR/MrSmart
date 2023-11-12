package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipRepository;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxRepository;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestTool;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolFormDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolRepository;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalRequestSheetService {

	private final Logger logger = LoggerFactory.getLogger(RentalRequestSheetService.class);
	
	private final RentalRequestSheetRepository repository;
	
	@Autowired
	private final RentalRequestToolRepository rentalRequestToolRepository;
	
	@Autowired
	private final MembershipRepository membershipRepository;
	
	@Autowired
	private final ToolboxRepository toolboxRepository;
	
	@Autowired
	private RentalRequestToolService rentalRequestToolService;
	
	
	@Transactional(readOnly = true)
	public RentalRequestSheetDto get(long id){
		Optional<RentalRequestSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return convertToDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public Page<RentalRequestSheetDto> getRentalRequestSheetPageByToolboxId( Pageable pageable , Long toolboxId) {
		Page<RentalRequestSheet> page = repository.findAllByToolboxId(toolboxId, pageable);	
		return page.map(e->convertToDto(e));
	}
	
	private RentalRequestSheetDto convertToDto(RentalRequestSheet rentalRequestSheet) {
		List<RentalRequestTool> toolList = rentalRequestToolRepository.findAllByRentalRequestSheetId(rentalRequestSheet.getId());
		return new RentalRequestSheetDto(rentalRequestSheet,toolList);
	}

	@Transactional
	public RentalRequestSheetDto addNew(RentalRequestSheetFormDto formDto) {
		
		Optional<Membership> worker = membershipRepository.findById(formDto.getWorkerDtoId());
		Optional<Membership> leader = membershipRepository.findById(formDto.getLeaderDtoId());
		Optional<Toolbox> toolbox = toolboxRepository.findById(formDto.getToolboxDtoId());

		logger.debug("worker : " + worker + "\r\n" + "leader : " + leader + "\r\n" + "toolbox : " + toolbox);
		
		RentalRequestSheet rentalRequestSheet = RentalRequestSheet.builder()
			.worker(worker.get())
			.leader(leader.get())
			.status(SheetState.REQUEST)
			.toolbox(toolbox.get())
			.eventTimestamp(LocalDateTime.now())
			.build();
		
		RentalRequestSheet savedRentalRequestSheet = repository.save(rentalRequestSheet);
		
		List<RentalRequestTool> toolList = new ArrayList<RentalRequestTool>();
		for (RentalRequestToolFormDto tool : formDto.getToolList()) {
			RentalRequestTool newTool = rentalRequestToolService.addNew(tool, rentalRequestSheet);
			toolList.add(newTool);
		}
		
		
		return new RentalRequestSheetDto(savedRentalRequestSheet,toolList);
	}
}

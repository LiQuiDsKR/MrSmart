package com.care4u.toolbox.sheet.rental.rental_sheet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipRepository;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxRepository;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheet;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestTool;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolFormDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolRepository;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolService;
import com.care4u.toolbox.sheet.supply_tool.SupplyTool;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalSheetService {

	private final Logger logger = LoggerFactory.getLogger(RentalSheetService.class);
	
	private final RentalSheetRepository repository;
	private final MembershipRepository membershipRepository;
	private final ToolboxRepository toolboxRepository;
	private final RentalToolRepository rentalToolRepository;
	private final RentalToolService rentalToolService;
	
	@Transactional(readOnly = true)
	public RentalSheetDto get(long id){
		Optional<RentalSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return convertToDto(item.get());
	}
	
	private RentalSheetDto convertToDto(RentalSheet rentalSheet) {
		List<RentalToolDto> dtoList = new ArrayList<RentalToolDto>();
		List<RentalTool> toolList = rentalToolRepository.findAllByRentalSheetId(rentalSheet.getId());
		for (RentalTool tool : toolList) {
			dtoList.add(new RentalToolDto(tool));
		}
		return new RentalSheetDto(rentalSheet,dtoList);
	}
	
	@Transactional
	public RentalSheetDto addNew(RentalRequestSheetDto requestSheetDto, long approverId) {
		Optional<Membership> worker = membershipRepository.findById(requestSheetDto.getWorkerDto().getId());
		Optional<Membership> leader = membershipRepository.findById(requestSheetDto.getLeaderDto().getId());
		Optional<Membership> approver = membershipRepository.findById(approverId);
		Optional<Toolbox> toolbox = toolboxRepository.findById(requestSheetDto.getToolboxDto().getId());
		
		if (worker.isEmpty()) {
			logger.debug("worker : " + worker);
			return null;
		}
		if (leader.isEmpty()) {
			logger.debug("leader :" + leader);
			return null;
		}
		if (approver.isEmpty()) {
			logger.debug("approver :" + approver);
			return null;
		}
		if (toolbox.isEmpty()) {
			logger.debug("toolbox : " + toolbox);
			return null;
		}
		
		RentalSheet rentalSheet = RentalSheet.builder()
				.worker(worker.get())
				.leader(leader.get())
				.approver(approver.get())
				.toolbox(toolbox.get())
				.eventTimestamp(LocalDateTime.now())
				.build()
				;
		
		RentalSheet savedRentalSheet = repository.save(rentalSheet);
		
		List<RentalTool> toolList = new ArrayList<RentalTool>();
		for (RentalRequestToolDto tool : requestSheetDto.getToolList()) {
			RentalTool newTool = rentalToolService.addNew(tool, savedRentalSheet);
			if (newTool.getTool().getSubGroup().getMainGroup().getName().equals("소모자재")) {
				logger.info("must be added to supplySheet, not rentalSheet.");
			}
			toolList.add(newTool);
		}
		return convertToDto(savedRentalSheet);
	}

}

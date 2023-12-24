package com.care4u.toolbox.sheet.supply_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.hr.main_part.MainPart;
import com.care4u.hr.main_part.MainPartRepository;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipRepository;
import com.care4u.hr.part.Part;
import com.care4u.hr.part.PartRepository;
import com.care4u.hr.sub_part.SubPart;
import com.care4u.hr.sub_part.SubPartRepository;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxRepository;
import com.care4u.toolbox.sheet.rental.outstanding_rental_sheet.OutstandingRentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.supply_tool.SupplyTool;
import com.care4u.toolbox.sheet.supply_tool.SupplyToolDto;
import com.care4u.toolbox.sheet.supply_tool.SupplyToolRepository;
import com.care4u.toolbox.sheet.supply_tool.SupplyToolService;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.tool.ToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SupplySheetService {

	private final Logger logger = LoggerFactory.getLogger(SupplySheetService.class);
	
	private final SupplySheetRepository repository;
	private final SupplyToolService supplyToolService;
	private final SupplyToolRepository supplyToolRepository;
	private final MembershipRepository membershipRepository;
	private final ToolboxRepository toolboxRepository;
	private final PartRepository partRepository;
	private final SubPartRepository subPartRepository;
	private final MainPartRepository mainPartRepository;
	private final ToolRepository toolRepository;
	
	@Transactional(readOnly = true)
	public SupplySheetDto get(long id){
		Optional<SupplySheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return convertToDto(item.get());
	}

	private SupplySheetDto convertToDto(SupplySheet supplySheet) {
		List<SupplyToolDto> dtoList = new ArrayList<SupplyToolDto>();
		List<SupplyTool> toolList = supplyToolRepository.findAllBySupplySheetId(supplySheet.getId());
		for (SupplyTool tool : toolList) {
			dtoList.add(new SupplyToolDto(tool));
		}
		return new SupplySheetDto(supplySheet,dtoList);
	}
	
	@Transactional
	public SupplySheetDto addNew(RentalRequestSheetDto requestSheetDto, long approverId) {
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
		
		SupplySheet supplySheet = SupplySheet.builder()
				.worker(worker.get())
				.leader(leader.get())
				.approver(approver.get())
				.toolbox(toolbox.get())
				.eventTimestamp(LocalDateTime.now())
				.build()
				;
		
		SupplySheet savedSupplySheet = repository.save(supplySheet);
		
		List<SupplyTool> toolList = new ArrayList<SupplyTool>();
		for (RentalRequestToolDto tool : requestSheetDto.getToolList()) {
			SupplyTool newTool = supplyToolService.addNew(tool, savedSupplySheet);
			if (!newTool.getTool().getSubGroup().getMainGroup().getName().equals("소모자재")) {
			}
			toolList.add(newTool);
		}
		return convertToDto(savedSupplySheet);
	}
	
	@Transactional
	public SupplySheetDto addNew(RentalRequestSheetDto requestSheetDto,List<RentalRequestToolDto> supplyRequestToolList, long approverId) {
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
		
		SupplySheet supplySheet = SupplySheet.builder()
				.worker(worker.get())
				.leader(leader.get())
				.approver(approver.get())
				.toolbox(toolbox.get())
				.eventTimestamp(LocalDateTime.now())
				.build()
				;
		
		SupplySheet savedSupplySheet = repository.save(supplySheet);
		
		List<SupplyTool> toolList = new ArrayList<SupplyTool>();
		for (RentalRequestToolDto tool : supplyRequestToolList) {
			SupplyTool newTool = supplyToolService.addNew(tool, savedSupplySheet);
			toolList.add(supplyToolService.addNew(tool, savedSupplySheet));
			logger.info(newTool.getTool().getName()+" added to SupplySheet:"+savedSupplySheet.getId());
		}
		
		return convertToDto(savedSupplySheet);
	}

	public Page<SupplySheetDto> getPage(Long partId, Long membershipId, Boolean isWorker, Boolean isLeader,
			Boolean isApprover, Long toolId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
		Optional<Part> part = partRepository.findById(partId);
		if (part.isEmpty()) {
			Optional<SubPart> subPart = subPartRepository.findById(partId);
			if (subPart.isEmpty()) {
				Optional<MainPart> mainPart = mainPartRepository.findById(partId);
				if (mainPart.isEmpty()) {
					logger.info("no part : " +partId + " all part selected.");
				}
			}
		}
		Optional<Membership> membershipOptional = membershipRepository.findById(membershipId);
		Membership membership;
		if (membershipOptional.isEmpty()) {
			logger.info("no membership : " +membershipId + " all membership selected.");
			membership=null;
		}else {
			membership=membershipOptional.get();
		}
		
		Optional<Tool> toolOptional = toolRepository.findById(toolId);
		Tool tool;
		if (toolOptional.isEmpty()) {
			logger.info("no tool : " +toolId + " all tool selected.");
			tool=null;
		}else {
			tool=toolOptional.get();
		}
		
		Page<SupplySheet> page = repository.findBySearchQuery(partId, membership, isWorker, isLeader, isApprover, tool, LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX), pageable);
		
		return page.map(e -> convertToDto(e));
	}
}

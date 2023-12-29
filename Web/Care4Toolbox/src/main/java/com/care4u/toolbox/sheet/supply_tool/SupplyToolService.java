package com.care4u.toolbox.sheet.supply_tool;

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
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.supply_sheet.SupplySheet;
import com.care4u.toolbox.sheet.supply_sheet.SupplySheetRepository;
import com.care4u.toolbox.stock_status.StockStatusDto;
import com.care4u.toolbox.stock_status.StockStatusService;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.tool.ToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SupplyToolService {

	private final Logger logger = LoggerFactory.getLogger(SupplyToolService.class);
	
	private final SupplyToolRepository repository;
	private final ToolRepository toolRepository;
	private final StockStatusService stockStatusService;
	private final SupplyToolService supplyToolService;
	private final SupplyToolRepository supplyToolRepository;
	private final MembershipRepository membershipRepository;
	private final ToolboxRepository toolboxRepository;
	private final PartRepository partRepository;
	private final SubPartRepository subPartRepository;
	private final MainPartRepository mainPartRepository;
	
	@Transactional(readOnly = true)
	public SupplyToolDto get(long id){
		Optional<SupplyTool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new SupplyToolDto(item.get());
	}

	@Transactional
	public SupplyTool addNew(RentalRequestToolDto requestDto, SupplySheet sheet) {
		Optional<Tool> tool = toolRepository.findById(requestDto.getToolDto().getId());
		if (tool.isEmpty()){
			logger.error("tool not found");
			return null;
		}
		
		SupplyTool supplyTool = SupplyTool.builder()
				.supplySheet(sheet)
				.tool(tool.get())
				.count(requestDto.getCount())
				.replacementDate(LocalDate.now().plusMonths(tool.get().getReplacementCycle()))
				.build();
		
		StockStatusDto stockDto = stockStatusService.get(supplyTool.getTool().getId(),sheet.getToolbox().getId());
		stockStatusService.supplyItems(stockDto.getId(), supplyTool.getCount());
		
		return repository.save(supplyTool);
	}

	public Page<SupplyToolDto> getPage(Long partId, Long membershipId, Boolean isWorker, Boolean isLeader,
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
		
		return page.map(e -> new SupplyToolDto(e));
		return null;
	}
}

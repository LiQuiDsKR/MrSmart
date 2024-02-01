package com.care4u.toolbox.sheet.buy_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

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
import com.care4u.toolbox.sheet.buy_tool.BuyTool;
import com.care4u.toolbox.sheet.buy_tool.BuyToolDto;
import com.care4u.toolbox.sheet.buy_tool.BuyToolFormDto;
import com.care4u.toolbox.sheet.buy_tool.BuyToolRepository;
import com.care4u.toolbox.sheet.buy_tool.BuyToolService;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheet;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestTool;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolFormDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BuySheetService {

	private final Logger logger = LoggerFactory.getLogger(BuySheetService.class);
	
	private final BuySheetRepository repository;
	private final MembershipRepository membershipRepository;
	private final ToolboxRepository toolboxRepository;
	private final BuyToolRepository buyToolRepository;
	
	private final BuyToolService buyToolService;
	
	@Transactional(readOnly = true)
	public BuySheetDto get(long id){
		Optional<BuySheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return convertToDto(item.get());
	}

	@Transactional(readOnly=true)
	private BuySheetDto convertToDto(BuySheet sheet) {
		List<BuyToolDto> dtoList = new ArrayList<BuyToolDto>();
		List<BuyTool> toolList = buyToolRepository.findAllByBuySheetId(sheet.getId());
		for (BuyTool tool : toolList) {
			dtoList.add(new BuyToolDto(tool));
		}
		return new BuySheetDto(sheet,dtoList);
	}

	@Transactional
	public BuySheetDto addNew(@Valid BuySheetFormDto formDto) {

		Optional<Membership> approver = membershipRepository.findById(formDto.getApproverDtoId());
		Optional<Toolbox> toolbox = toolboxRepository.findById(formDto.getToolboxDtoId());
		if (approver.isEmpty()) {
			logger.debug("worker : " + approver);
			return null;
		}
		if (toolbox.isEmpty()) {
			logger.debug("toolbox : " + toolbox);
			return null;
		}

		logger.debug("worker : " + approver + "\r\n" + "toolbox : " + toolbox);
		
		BuySheet buySheet = BuySheet.builder()
				.approver(approver.get())
				.toolbox(toolbox.get())
				.eventTimestamp(LocalDateTime.now())
				.build();
		
		BuySheet savedBuySheet = repository.save(buySheet);
		
		List<BuyTool> toolList = new ArrayList<BuyTool>();
		for (BuyToolFormDto tool : formDto.getToolList()) {
			BuyTool newTool = buyToolService.addNew(tool, savedBuySheet);
			toolList.add(newTool);
		}
		return convertToDto(savedBuySheet);
	}

	@Transactional(readOnly = true)
	public Page<BuySheetDto> getPage(long toolboxId, LocalDate startDate, LocalDate endDate, Pageable pageable){
		Optional<Toolbox> toolboxOptional = toolboxRepository.findById(toolboxId);
		if(toolboxOptional.isEmpty()) {
			logger.error("invalid toolbox id : "+toolboxId);
			return null;
		}
		Page<BuySheet> page = repository.findByToolboxIdAndEventTimestampBetween(toolboxId,LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX), pageable);
		return page.map(e->convertToDto(e));
	}

}

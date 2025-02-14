package com.care4u.toolbox.sheet.rental.rental_sheet;

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

import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipRepository;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxRepository;
import com.care4u.toolbox.sheet.rental.outstanding_rental_sheet.OutstandingRentalSheetService;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheet;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetDto;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetService;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestTool;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolFormDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolRepository;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolService;
import com.care4u.toolbox.sheet.supply_tool.SupplyTool;
import com.care4u.toolbox.tag.Tag;
import com.care4u.toolbox.tag.TagRepository;

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
	private final TagRepository tagRepository;
	private final RentalToolService rentalToolService;
	private final OutstandingRentalSheetService outstandingRentalSheetService;
	private final RentalRequestSheetService rentalRequestSheetService;
	
	@Transactional(readOnly = true)
	public RentalSheetDto get(long id){
		Optional<RentalSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return convertToDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public Page<RentalSheetDto> getPage(long membershipId, LocalDate startDate, LocalDate endDate, Pageable pageable){
		Optional<Membership> membershipOptional = membershipRepository.findById(membershipId);
		if(membershipOptional.isEmpty()) {
			logger.error("invalid membership id : "+membershipId);
			return null;
		}
		Membership member = membershipOptional.get();
		Page<RentalSheet> page = repository.findByMemberAndEventTimestampBetween(member,LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX), pageable);
		return page.map(e->convertToDto(e));
	}
	
	@Transactional
	private RentalSheetDto convertToDto(RentalSheet rentalSheet) {
		List<RentalToolDto> dtoList = new ArrayList<RentalToolDto>();
		List<RentalTool> toolList = rentalToolRepository.findAllByRentalSheetId(rentalSheet.getId());
		for (RentalTool tool : toolList) {
			List<Tag> tagList = tagRepository.findAllByRentalToolId(tool.getId());
			dtoList.add(new RentalToolDto(tool,tagList));
		}
		return new RentalSheetDto(rentalSheet,dtoList);
	}
	
	
	/**
	 * RentalSheet, RentalTool, OutstandingRentalSheet를 전부 생성합니다.
	 * RentalTool 생성 시 Tags가 생성되며, StockStatus가 변경됩니다.
	 * @param requestSheetDto
	 * @param approverId
	 * @return rentalSheetDto
	 */
	@Transactional
	public RentalSheetDto addNew(RentalRequestSheetDto requestSheetDto, long approverId) {
		Optional<Membership> worker = membershipRepository.findById(requestSheetDto.getWorkerDto().getId());
		Optional<Membership> leader = membershipRepository.findById(requestSheetDto.getLeaderDto().getId());
		Optional<Membership> approver = membershipRepository.findById(approverId);
		Optional<Toolbox> toolbox = toolboxRepository.findById(requestSheetDto.getToolboxDto().getId());
		
	    if (worker.isEmpty()) {
	        logger.debug("Worker not found");
	        throw new IllegalArgumentException("Worker not found");
	    }
	    if (leader.isEmpty()) {
	        logger.debug("Leader not found");
	        throw new IllegalArgumentException("Leader not found");
	    }
	    if (approver.isEmpty()) {
	        logger.debug("Approver not found");
	        throw new IllegalArgumentException("Approver not found");
	    }
	    if (toolbox.isEmpty()) {
	        logger.debug("Toolbox not found");
	        throw new IllegalArgumentException("Toolbox not found");
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
		RentalSheetDto result=convertToDto(savedRentalSheet);
		
		outstandingRentalSheetService.addNew(savedRentalSheet, toolList);
		
		return result;
	}
	
	/**
	 * Controller에서 RentalRequestSheet update()와 RentalSheet addNew()를 따로 호출했을 때 생긴 문제를 방지하기 위해
	 * 하나의 transaction으로 묶어주기 위한 함수입니다.
	 * @param sheetDto
	 * @param approverId
	 * @return RentalSheetDto를 반환합니다.
	 */
	@Transactional
	public RentalSheetDto updateAndAddNewInTransaction(RentalRequestSheetDto sheetDto, long approverId) {
		// 1. RequestSheet를 Update
		rentalRequestSheetService.update(sheetDto,SheetState.APPROVE);
		// 2. RentalSheet를 addNew
		return addNew(sheetDto,approverId);
	}
	
}

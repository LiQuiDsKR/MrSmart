package com.care4u.toolbox.sheet.return_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.care4u.toolbox.sheet.rental.outstanding_rental_sheet.OutstandingRentalSheetService;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetService;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetRepository;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetService;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolRepository;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolService;
import com.care4u.toolbox.sheet.return_tool.ReturnTool;
import com.care4u.toolbox.sheet.return_tool.ReturnToolDto;
import com.care4u.toolbox.sheet.return_tool.ReturnToolFormDto;
import com.care4u.toolbox.sheet.return_tool.ReturnToolRepository;
import com.care4u.toolbox.sheet.return_tool.ReturnToolService;
import com.care4u.toolbox.tag.Tag;
import com.care4u.toolbox.tag.TagRepository;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReturnSheetService {

	private final Logger logger = LoggerFactory.getLogger(ReturnSheetService.class);
	
	private final ReturnSheetRepository repository;
	private final MembershipRepository membershipRepository;
	private final ToolboxRepository toolboxRepository;
	private final RentalToolRepository rentalToolRepository;
	private final TagRepository tagRepository;
	private final RentalSheetRepository rentalSheetRepository;
	private final ReturnToolRepository returnToolRepository;
	private final PartRepository partRepository;
	private final SubPartRepository subPartRepository;
	private final MainPartRepository mainPartRepository;
	private final ToolRepository toolRepository;
	private final ReturnToolService returnToolService;
	private final OutstandingRentalSheetService outstandingRentalSheetService;
	private final RentalRequestSheetService rentalRequestSheetService;
	private final RentalSheetService rentalSheetService;
	
	@Transactional(readOnly = true)
	public ReturnSheetDto get(long id){
		Optional<ReturnSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return convertToDto(item.get());
	}

	@Transactional(readOnly = true)
	private ReturnSheetDto convertToDto(ReturnSheet returnSheet) {
		RentalSheetDto rentalSheetDto = rentalSheetService.convertToDto(returnSheet.getRentalSheet());
		List<ReturnToolDto> dtoList = new ArrayList<ReturnToolDto>();
		List<ReturnTool> toolList = returnToolRepository.findAllByReturnSheetId(returnSheet.getId());
		for (ReturnTool tool : toolList) {
			List<Tag> rentalTagList = tagRepository.findAllByRentalToolId(tool.getRentalTool().getId());
			String rentalTags = rentalTagList.stream()
					.map(tag -> tag.getMacaddress())
					.collect(Collectors.joining(","));
			dtoList.add(new ReturnToolDto(tool,rentalTags));
		}
		
		return new ReturnSheetDto(returnSheet,rentalSheetDto.getToolList(),dtoList);
	}
	
	@Transactional(readOnly = true)
	public Page<ReturnSheetDto> getPage(Long partId, long membershipId, Boolean isWorker, Boolean isLeader,
			Boolean isApprover, long toolId, LocalDate startLocalDate, LocalDate endLocalDate, Pageable pageable) {
		Optional<Part> part = partRepository.findById(partId);
		if (part.isEmpty()) {
			Optional<SubPart> subPart = subPartRepository.findById(partId);
			if (subPart.isEmpty()) {
				Optional<MainPart> mainPart = mainPartRepository.findById(partId);
				if (mainPart.isEmpty()) {
					logger.info("no part : " + partId + " all part selected.");
				}
			}
		}
		Optional<Membership> membershipOptional = membershipRepository.findById(membershipId);
		Membership membership;
		if (membershipOptional.isEmpty()) {
			logger.info("no membership : " + membershipId + " all membership selected.");
			membership = null;
		} else {
			membership = membershipOptional.get();
		}

		Optional<Tool> toolOptional = toolRepository.findById(toolId);
		Tool tool;
		if (toolOptional.isEmpty()) {
			logger.info("no tool : " + toolId + " all tool selected.");
			tool = null;
		} else {
			tool = toolOptional.get();
		}

		Page<ReturnSheet> page = repository.findBySearchQuery(partId, membership, isWorker, isLeader, isApprover, tool,
				LocalDateTime.of(startLocalDate, LocalTime.MIN), LocalDateTime.of(endLocalDate, LocalTime.MAX), pageable);

		return page.map(e->convertToDto(e));
	}
	
	/**
	 * 다음 순서로 업데이트 됩니다.
	 * returnSheet ->
	 * [
	 * 		returnTool -> 
	 * 			[
	 * 				tag,
	 * 				stockStatus,
	 * 				rentalTool
	 * 			]
	 * 		outstandingSheet
	 * ]
	 * 
	 * @param formDto
	 */
	@Transactional
	public ReturnSheetDto addNew(ReturnSheetFormDto formDto) {
		Optional<RentalSheet> rentalSheet = rentalSheetRepository.findById(formDto.getRentalSheetDtoId());
		Optional<Membership> worker = membershipRepository.findById(formDto.getWorkerDtoId());
		Optional<Membership> approver = membershipRepository.findById(formDto.getApproverDtoId());
		Optional<Toolbox> toolbox = toolboxRepository.findById(formDto.getToolboxDtoId());
		
	    if (rentalSheet.isEmpty()) {
	        logger.debug("RentalSheet not found");
	        throw new IllegalArgumentException("RentalSheet not found");
	    }
	    if (worker.isEmpty()) {
	        logger.debug("Worker not found");
	        throw new IllegalArgumentException("Worker not found");
	    }
	    if (approver.isEmpty()) {
	        logger.debug("Approver not found");
	        throw new IllegalArgumentException("Approver not found");
	    }
	    if (toolbox.isEmpty()) {
	        logger.debug("Toolbox not found");
	        throw new IllegalArgumentException("Toolbox not found");
	    }
		
		ReturnSheet returnSheet = ReturnSheet.builder()
				.worker(worker.get())
				.rentalSheet(rentalSheet.get())
				.approver(approver.get())
				.toolbox(toolbox.get())
				.eventTimestamp(LocalDateTime.now())
				.build()
				;
		
		ReturnSheet savedReturnSheet = repository.save(returnSheet);
		
		List<ReturnTool> toolList = new ArrayList<ReturnTool>();
		for (ReturnToolFormDto tool : formDto.getToolList()) {
			ReturnTool newTool = returnToolService.addNew(tool, savedReturnSheet);
			toolList.add(newTool);
		}
		
		ReturnSheetDto resultDto=convertToDto(savedReturnSheet);
		
		outstandingRentalSheetService.update(resultDto);
		
		return resultDto;
	}

	public Page<ReturnSheetDto> getPage(long membershipId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
		Optional<Membership> membershipOptional = membershipRepository.findById(membershipId);
		if(membershipOptional.isEmpty()) {
			logger.error("invalid membership id : "+membershipId);
			return null;
		}
		Membership member = membershipOptional.get();
		Page<ReturnSheet> page = repository.findByMemberAndEventTimestampBetween(member,LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX), pageable);
		return page.map(e->convertToDto(e));
	}
	
	@Transactional
	public ReturnSheetDto addNew(ReturnSheetFormDto formDto,LocalDateTime eventTimestamp) {
		ReturnSheet findSheet = repository.findByEventTimestamp(eventTimestamp);
		if (findSheet != null) {
			logger.error("returnSheet already exists! : " + eventTimestamp);
			return null;
		}else {
			return addNew(formDto);
		}
	}
}

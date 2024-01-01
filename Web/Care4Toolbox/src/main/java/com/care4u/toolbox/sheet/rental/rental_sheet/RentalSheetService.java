package com.care4u.toolbox.sheet.rental.rental_sheet;

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

import com.care4u.constant.SheetState;
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
import com.care4u.toolbox.sheet.return_sheet.ReturnSheet;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetDto;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetFormDto;
import com.care4u.toolbox.sheet.supply_sheet.SupplySheetService;
import com.care4u.toolbox.sheet.supply_tool.SupplyTool;
import com.care4u.toolbox.tag.Tag;
import com.care4u.toolbox.tag.TagRepository;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.tool.ToolRepository;

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
	private final ToolRepository toolRepository;
	private final PartRepository partRepository;
	private final SubPartRepository subPartRepository;
	private final MainPartRepository mainPartRepository;
	private final RentalToolService rentalToolService;
	private final OutstandingRentalSheetService outstandingRentalSheetService;
	private final RentalRequestSheetService rentalRequestSheetService;
	private final SupplySheetService supplySheetService;

	@Transactional(readOnly = true)
	public RentalSheetDto get(long id) {
		Optional<RentalSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}

		return convertToDto(item.get());
	}

	/*
	 * @Transactional(readOnly = true) public Page<RentalSheetDto> getPage(long
	 * membershipId, LocalDate startDate, LocalDate endDate, Pageable pageable){
	 * Optional<Membership> membershipOptional =
	 * membershipRepository.findById(membershipId); if(membershipOptional.isEmpty())
	 * { logger.error("invalid membership id : "+membershipId); return null; }
	 * Membership member = membershipOptional.get(); Page<RentalSheet> page =
	 * repository.findByMemberAndEventTimestampBetween(member,LocalDateTime.of(
	 * startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX),
	 * pageable); return page.map(e->convertToDto(e)); }
	 */
	@Transactional(readOnly = true)
	public Page<RentalSheetDto> getPage(Long partId, long membershipId, Boolean isWorker, Boolean isLeader,
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

		Page<RentalSheet> page = repository.findBySearchQuery(partId, membership, isWorker, isLeader, isApprover, tool,
				LocalDateTime.of(startLocalDate, LocalTime.MIN), LocalDateTime.of(endLocalDate, LocalTime.MAX), pageable);

		return page.map(e->convertToDto(e));
	}

	@Transactional(readOnly = true)
	public RentalSheetDto convertToDto(RentalSheet rentalSheet) {
		List<RentalToolDto> dtoList = new ArrayList<RentalToolDto>();
		List<RentalTool> toolList = rentalToolRepository.findAllByRentalSheetId(rentalSheet.getId());
		for (RentalTool tool : toolList) {
			List<Tag> tagList = tagRepository.findAllByRentalToolId(tool.getId());
			String tags = tagList.stream().map(tag -> tag.getMacaddress()).collect(Collectors.joining(","));
			dtoList.add(new RentalToolDto(tool, tags));
		}
		return new RentalSheetDto(rentalSheet, dtoList);
	}

	/**
	 * 다음 순서로 업데이트 됩니다. rentalSheet -> [ rentalTool -> [ tag, stockStatus, ]
	 * supplySheet -> [ supplyTool -> [ stockStatus ] ] outstandingRentalSheet ]
	 * 
	 * @param formDto
	 */
	@Transactional
	public RentalSheetDto addNew(RentalRequestSheetDto requestSheetDto, long approverId) {

		// parameter null check
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

		// sheet create
		RentalSheet rentalSheet = RentalSheet.builder().worker(worker.get()).leader(leader.get())
				.approver(approver.get()).toolbox(toolbox.get()).eventTimestamp(LocalDateTime.now()).build();
		RentalSheet savedRentalSheet = repository.save(rentalSheet);

		// tool create
		List<RentalTool> toolList = new ArrayList<RentalTool>();
		List<RentalRequestToolDto> supplyRequestToolList = new ArrayList<RentalRequestToolDto>();
		for (RentalRequestToolDto tool : requestSheetDto.getToolList()) {
			// supplyTool
			if (tool.getToolDto().getSubGroupDto().getMainGroupDto().getName().equals("소모자재")) {
				supplyRequestToolList.add(tool);
				continue;
			}
			// rentalTool
			RentalTool newTool = rentalToolService.addNew(tool, savedRentalSheet, requestSheetDto);
			toolList.add(newTool);
			logger.info(newTool.getTool().getName() + " added to RentalSheet:" + savedRentalSheet.getId());
		}

		// supplySheet
		if (supplyRequestToolList.size() > 0) {
			supplySheetService.addNew(requestSheetDto, supplyRequestToolList, approverId);
		}

		// outstandingSheet
		if (toolList.isEmpty()) {
			repository.delete(savedRentalSheet);
			return null;
		} else {
			RentalSheetDto result = convertToDto(savedRentalSheet);

			outstandingRentalSheetService.addNew(savedRentalSheet, toolList);

			return result;
		}
	}

	/**
	 * Controller에서 RentalRequestSheet update()와 RentalSheet addNew()를 따로 호출했을 때 생긴
	 * 문제를 방지하기 위해 하나의 transaction으로 묶어주기 위한 함수입니다.
	 * 
	 * @param sheetDto
	 * @param approverId
	 * @return RentalSheetDto를 반환합니다.
	 */
	@Transactional
	public RentalSheetDto updateAndAddNewInTransaction(RentalRequestSheetDto sheetDto, long approverId) {
		// 1. RequestSheet를 Update
		rentalRequestSheetService.update(sheetDto, SheetState.APPROVE);
		// 2. RentalSheet를 addNew
		return addNew(sheetDto, approverId);
	}
	
	@Transactional
	public RentalSheetDto updateAndAddNewInTransaction(RentalRequestSheetDto sheetDto, long approverId,LocalDateTime eventTimestamp) {
		RentalSheet findSheet = repository.findByEventTimestamp(eventTimestamp);
		if (findSheet != null) {
			logger.error("RentalSheet already exists! : " + eventTimestamp);
			return null;
		}else {
			return updateAndAddNewInTransaction(sheetDto,approverId);
		}
	}
}

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

import com.care4u.constant.OutstandingState;
import com.care4u.constant.SheetState;
import com.care4u.exception.NoSuchElementFoundException;
import com.care4u.exception.OutOfStockException;
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
import com.care4u.toolbox.sheet.rental.outstanding_rental_sheet.OutstandingRentalSheet;
import com.care4u.toolbox.sheet.rental.outstanding_rental_sheet.OutstandingRentalSheetRepository;
import com.care4u.toolbox.sheet.rental.outstanding_rental_sheet.OutstandingRentalSheetService;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheet;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetApproveFormDto;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetDto;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetRepository;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetService;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestTool;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolApproveFormDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolFormDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolRepository;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolRepository;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolService;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheet;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetDto;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetFormDto;
import com.care4u.toolbox.sheet.supply_sheet.SupplySheet;
import com.care4u.toolbox.sheet.supply_sheet.SupplySheetDto;
import com.care4u.toolbox.sheet.supply_sheet.SupplySheetRepository;
import com.care4u.toolbox.sheet.supply_sheet.SupplySheetService;
import com.care4u.toolbox.sheet.supply_tool.SupplyTool;
import com.care4u.toolbox.sheet.supply_tool.SupplyToolRepository;
import com.care4u.toolbox.stock_status.StockStatus;
import com.care4u.toolbox.stock_status.StockStatusDto;
import com.care4u.toolbox.stock_status.StockStatusRepository;
import com.care4u.toolbox.stock_status.StockStatusService;
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
	private final StockStatusRepository stockStatusRepository;
	private final RentalRequestSheetRepository rentalRequestSheetRepository;
	private final RentalRequestToolRepository rentalRequestToolRepository;
	private final SupplySheetRepository supplySheetRepository;
	private final SupplyToolRepository supplyToolRepository;
	private final OutstandingRentalSheetRepository outstandingRentalSheetRepository;
	private final RentalToolService rentalToolService;
	private final OutstandingRentalSheetService outstandingRentalSheetService;
	private final RentalRequestSheetService rentalRequestSheetService;
	private final SupplySheetService supplySheetService;
	private final StockStatusService stockStatusService;

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

		logger.debug("RentalSheet [Add] : Start");
		
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

		logger.debug("RentalSheet [Add] : membership & toolbox Null check completed.");
		
		// sheet create
		RentalSheet rentalSheet = RentalSheet.builder().worker(worker.get()).leader(leader.get())
				.approver(approver.get()).toolbox(toolbox.get()).eventTimestamp(LocalDateTime.now()).build();
		RentalSheet savedRentalSheet = repository.save(rentalSheet);
		
		logger.debug("RentalSheet [Add] : RentalSheet("+savedRentalSheet.getId()+") saved");

		// tool create
		List<RentalTool> toolList = new ArrayList<RentalTool>();
		List<RentalRequestToolDto> supplyRequestToolList = new ArrayList<RentalRequestToolDto>();
		
		logger.debug("RentalSheet [Add] : RentalToolList Add Start");
		for (RentalRequestToolDto tool : requestSheetDto.getToolList()) {
			// supplyTool
			if (tool.getToolDto().getSubGroupDto().getMainGroupDto().getName().equals("소모자재")) {
				supplyRequestToolList.add(tool);
				logger.debug("RentalSheet [Add] : Supply Tool Skip");
				continue;
			}
			// rentalTool
			RentalTool newTool = rentalToolService.addNew(tool, savedRentalSheet, requestSheetDto);
			toolList.add(newTool);
			logger.info(newTool.getTool().getName() + " added to RentalSheet:" + savedRentalSheet.getId());
		}
		logger.debug("RentalSheet [Add] : RentalToolList Add Completed");

		// supplySheet
		if (supplyRequestToolList.size() > 0) {
			supplySheetService.addNew(requestSheetDto, supplyRequestToolList, approverId);
		}

		logger.debug("RentalSheet [Add] : Outstanding Add Start");
		// outstandingSheet
		if (toolList.isEmpty()) {
			logger.debug("RentalSheet [Add] : toolList Empty -> rentalSheet add canceled");
			repository.delete(savedRentalSheet);
			return null;
		} else {
			logger.debug("RentalSheet [Add] : Dto converting start");
			RentalSheetDto result = convertToDto(savedRentalSheet);
			logger.debug("RentalSheet [Add] : Dto converting Completed");
			
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
		Optional<RentalRequestSheet> sheet = rentalRequestSheetRepository.findById(sheetDto.getId());
		if (sheet.get().getStatus().equals(SheetState.APPROVE)) {
			logger.debug("Sheet already approved!");
			return null;
		}
		rentalRequestSheetService.update(sheetDto, SheetState.APPROVE);
		// 2. RentalSheet를 addNew
		return addNew(sheetDto, approverId);
	}
	@Transactional
	public RentalSheetDto updateAndAddNewInTransaction(RentalRequestSheetApproveFormDto formDto) {
		
		logger.debug("Request start");
		
		RentalRequestSheetDto sheetDto=rentalRequestSheetService.update(formDto, SheetState.APPROVE);
		return addNew(sheetDto, formDto.getApproverDtoId());
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
	@Transactional
	public RentalSheetDto updateAndAddNewInTransaction(RentalRequestSheetApproveFormDto formDto,LocalDateTime eventTimestamp) {
		RentalSheet findSheet = repository.findByEventTimestamp(eventTimestamp);
		if (findSheet != null) {
			logger.error("RentalSheet already exists! : " + eventTimestamp);
			return null;
		}else {
			Optional<RentalSheet> sheetOptional = repository.findById(updateAndAddNewInTransaction(formDto).getId());
			RentalSheet sheet = sheetOptional.get();
			sheet.updateEventTimestamp(eventTimestamp);
			repository.save(sheet);
			return null;
		}
	}
	
	@Transactional
	public RentalSheetDto create(RentalRequestSheetApproveFormDto formDto) {
		Optional<Membership> worker = membershipRepository.findById(formDto.getWorkerDtoId());
		Optional<Membership> leader = membershipRepository.findById(formDto.getLeaderDtoId());
		Optional<Membership> approver = membershipRepository.findById(formDto.getApproverDtoId());
		Optional<Toolbox> toolbox = toolboxRepository.findById(formDto.getToolboxDtoId());
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
		RentalRequestSheetDto sheetDto = rentalRequestSheetUpdate(formDto,SheetState.APPROVE,worker.get(),leader.get(),toolbox.get());
		return addNewPrivate(sheetDto,worker.get(),leader.get(),approver.get(),toolbox.get());
	}
	
	private RentalRequestSheetDto rentalRequestSheetUpdate(RentalRequestSheetApproveFormDto formDto, SheetState status, Membership worker,Membership leader,Toolbox toolbox) {
		logger.debug("RentalRequestSheet [Add] : start");
		RentalRequestSheet rentalRequestSheet;
		Optional<RentalRequestSheet> rentalRequestSheetOptional = rentalRequestSheetRepository.findById(formDto.getId());
		if (rentalRequestSheetOptional.isEmpty()) {
			logger.error("Sheet not found");
			throw new IllegalArgumentException("대여 승인이 되지 않았거나, 이미 반납된 시트입니다.");
		}
		if (rentalRequestSheetOptional.get().getStatus().equals(status)) {
			logger.debug("Sheet is already " + status);
			return null;
		}

		rentalRequestSheet = rentalRequestSheetOptional.get();

		logger.debug("RentalRequestSheet [Add] : Param Null check completed");

		rentalRequestSheet.update(worker, leader, toolbox, status,
				rentalRequestSheet.getEventTimestamp());

		List<RentalRequestToolDto> dtoList = new ArrayList<RentalRequestToolDto>();
		logger.debug("RentalRequestSheet [Add] : toolList start");
		for (RentalRequestToolApproveFormDto tool : formDto.getToolList()) {
			logger.debug("RentalRequestSheet [Add] : tool finding");
			Optional<RentalRequestTool> optionalRequestTool = rentalRequestToolRepository.findById(tool.getId());
			Optional<Tool> optionalTool = toolRepository.findById(tool.getToolDtoId());
			if (optionalRequestTool.isEmpty()) {
				logger.error("rental request tool not found");
				throw new IllegalArgumentException("ID : "+tool.getId()+"의 공기구 대여 신청 정보를 찾을 수 없습니다.");
			}
			if (optionalTool.isEmpty()) {
				logger.error("tool not found");
				throw new IllegalArgumentException("ID : "+tool.getToolDtoId()+"의 공기구 정보를 찾을 수 없습니다.");
			}
			logger.debug("RentalRequestSheet [Add] : tool found");
			RentalRequestToolDto newDto = RentalRequestToolDto.builder().id(tool.getId()).count(tool.getCount())
					.toolDto(new ToolDto(optionalTool.get())).Tags(tool.getTags()).build();

			dtoList.add(newDto);
			logger.debug("RentalRequestSheet [Add] : tool add");
		}
		logger.debug("RentalRequestSheet [Add] : complete");
		return new RentalRequestSheetDto(rentalRequestSheetRepository.save(rentalRequestSheet), dtoList);
	}
	
	private RentalSheetDto addNewPrivate(RentalRequestSheetDto requestSheetDto, Membership worker,Membership leader,Membership approver,Toolbox toolbox) {

		logger.debug("RentalSheet [Add] : Start");
		
		// sheet create
		RentalSheet rentalSheet = RentalSheet.builder().worker(worker).leader(leader)
				.approver(approver).toolbox(toolbox).eventTimestamp(LocalDateTime.now()).build();
		RentalSheet savedRentalSheet = repository.save(rentalSheet);
		
		logger.debug("RentalSheet [Add] : RentalSheet("+savedRentalSheet.getId()+") saved");

		// tool create
		List<RentalTool> toolList = new ArrayList<RentalTool>();
		List<RentalRequestToolDto> supplyRequestToolList = new ArrayList<RentalRequestToolDto>();
		List<String> toolOutOfStockExceptionList = new ArrayList<String>();
		List<String> toolNotFoundExceptionList = new ArrayList<String>();
		
		logger.debug("RentalSheet [Add] : RentalToolList Add Start");
		
		for (RentalRequestToolDto tool : requestSheetDto.getToolList()) {
			// supplyTool
			if (tool.getToolDto().getSubGroupDto().getMainGroupDto().getName().equals("소모자재")) {
				supplyRequestToolList.add(tool);
				logger.debug("RentalSheet [Add] : Supply Tool Skip");
				continue;
			}
			// rentalTool
			try {
			RentalTool newTool = addNewRentalTool(tool, savedRentalSheet, requestSheetDto);
			toolList.add(newTool);
			logger.info(newTool.getTool().getName() + " added to RentalSheet:" + savedRentalSheet.getId());
			} catch (IllegalArgumentException e) {
                toolNotFoundExceptionList.add(e.getMessage()+" ");
            } catch (NoSuchElementFoundException e) {
                toolNotFoundExceptionList.add(e.getMessage()+" ");
            } catch (OutOfStockException e) {
				toolOutOfStockExceptionList.add(e.getMessage()+" ");
			}
		}
		if (!toolOutOfStockExceptionList.isEmpty()) {
			logger.error("Stock out of stock : " + toolOutOfStockExceptionList.toString());
			throw new OutOfStockException(toolOutOfStockExceptionList.toString()+ "재고가 부족합니다.");
		}
		if (!toolNotFoundExceptionList.isEmpty()) {
			logger.error("Tool not found : " + toolNotFoundExceptionList.toString());
			throw new IllegalArgumentException(toolNotFoundExceptionList.toString() + "공기구 정보를 찾을 수 없습니다.");
		}
		logger.debug("RentalSheet [Add] : RentalToolList Add Completed");

		// supplySheet
		if (supplyRequestToolList.size() > 0) {
			addNewSupplySheet(requestSheetDto, supplyRequestToolList, worker, leader, approver, toolbox);
		}

		logger.debug("RentalSheet [Add] : Outstanding Add Start");
		// outstandingSheet
		if (toolList.isEmpty()) {
			// supply만 있는 경우.
			logger.debug("RentalSheet [Add] : toolList Empty -> rentalSheet add canceled");
			repository.delete(savedRentalSheet);
            return null;
		} else {
			logger.debug("RentalSheet [Add] : Dto converting start");
			RentalSheetDto result = convertToDto(savedRentalSheet);
			logger.debug("RentalSheet [Add] : Dto converting Completed");
			
			addNewOutstandingRentalSheet(savedRentalSheet, toolList);

			return result;
		}
	}
	private RentalTool addNewRentalTool(RentalRequestToolDto requestDto, RentalSheet sheet, RentalRequestSheetDto requestSheetDto) {
		logger.debug("RentalTool [Add] : start");
		Optional<Tool> tool = toolRepository.findById(requestDto.getToolDto().getId());
		if (tool.isEmpty()){
			logger.error("tool not found");
			throw new IllegalArgumentException("ID : "+requestDto.getToolDto().getId()+","+requestDto.getToolDto().getName()+","+requestDto.getToolDto().getSpec()+" 공기구 정보를 찾을 수 없습니다.");
		}
		Optional<RentalRequestSheet> requestSheet = rentalRequestSheetRepository.findById(requestSheetDto.getId());
		if (requestSheet.isEmpty()){
			logger.error("requestSheet not found");
			throw new NoSuchElementFoundException("ID : "+requestSheetDto.getId()+"의 대여 신청 정보를 찾을 수 없습니다.");
		}
		logger.debug("RentalTool [Add] : tool & sheet Null check completed.");
		
		RentalTool rentalTool = RentalTool.builder()
				.rentalSheet(sheet)
				.tool(tool.get())
				.count(requestDto.getCount())
				.outstandingCount(requestDto.getCount())
				//.rentalRequestSheet(requestSheet.get())
				.build();
		
		RentalTool savedRentalTool=rentalToolRepository.save(rentalTool);
		
		logger.debug("RentalTool [Add] : RentalTool("+savedRentalTool.getId()+") saved");
		
		logger.debug("RentalTool [Add] : Tag info upload start");
		if (requestDto.getTags() != null && requestDto.getTags().length() > 0) {
			String[] tags = requestDto.getTags().split(",");
			for (String tagString : tags) {
				Tag tag = tagRepository.findByMacaddress(tagString);
				if (tag==null) {
					logger.error("Tag : "+tagString+" not found");
					throw new NoSuchElementFoundException("Qr코드 : "+tagString+"는 등록되지 않은 코드입니다.");
				}
					tag.updateRentalTool(savedRentalTool);
					logger.info(tag.getMacaddress()+" added to "+savedRentalTool.getId()+":"+savedRentalTool.getTool().getName());
			}
		} else {
			logger.debug("RentalTool [Add] : No tag info");
		}
		
		//StockStatusDto stockDto = stockStatusService.get(savedRentalTool.getTool().getId(),sheet.getToolbox().getId());
		try {
			StockStatus stock = stockStatusRepository.findByToolIdAndToolboxIdAndCurrentDay(
					savedRentalTool.getTool().getId(), sheet.getToolbox().getId(), LocalDate.now());
			stockStatusService.rentItems(stock.getId(), savedRentalTool.getCount());
		} catch (IllegalArgumentException e) {
			logger.error("Stock not found");
			throw new NoSuchElementFoundException(tool.get().getName()+","+tool.get().getSpec());
		} catch (NoSuchElementFoundException e) {
			logger.error("Stock not found");
			throw new NoSuchElementFoundException(tool.get().getName() + "," + tool.get().getSpec());
		} catch (OutOfStockException e) {
			logger.error("Stock out of stock");
			throw new OutOfStockException(tool.get().getName() + "," + tool.get().getSpec());
		}
		
		logger.debug("RentalTool [Add] : Completed");
		return savedRentalTool;
	}
	
	private void addNewSupplySheet(RentalRequestSheetDto requestSheetDto,List<RentalRequestToolDto> supplyRequestToolList, Membership worker,Membership leader,Membership approver,Toolbox toolbox) {
		logger.debug("SupplySheet [Add] : Start");
		
		SupplySheet supplySheet = SupplySheet.builder()
				.worker(worker)
				.leader(leader)
				.approver(approver)
				.toolbox(toolbox)
				.eventTimestamp(LocalDateTime.now())
				.build()
				;
		
		SupplySheet savedSupplySheet = supplySheetRepository.save(supplySheet);
		
		logger.debug("SupplySheet [Add] : SupplySheet("+savedSupplySheet.getId()+") saved");
		
		List<String> toolOutOfStockExceptionList = new ArrayList<String>();
		List<String> toolNotFoundExceptionList = new ArrayList<String>();
		
		logger.debug("SupplySheet [Add] : SupplyToolList Add Start");
		for (RentalRequestToolDto tool : supplyRequestToolList) {
			try {
				SupplyTool newTool = addNewSupplyTool(tool, savedSupplySheet);
				logger.info(newTool.getTool().getName()+" added to SupplySheet:"+savedSupplySheet.getId());
			} catch (IllegalArgumentException e) {
				toolNotFoundExceptionList.add(e.getMessage() + " ");
			} catch (NoSuchElementFoundException e) {
				toolNotFoundExceptionList.add(e.getMessage() + " ");
			} catch (OutOfStockException e) {
				toolOutOfStockExceptionList.add(e.getMessage() + " ");
			}
		}
		if (!toolOutOfStockExceptionList.isEmpty()) {
			logger.error("Stock out of stock : " + toolOutOfStockExceptionList.toString());
			throw new OutOfStockException(toolOutOfStockExceptionList.toString() + "재고가 부족합니다.");
		}
		if (!toolNotFoundExceptionList.isEmpty()) {
			logger.error("Tool not found : " + toolNotFoundExceptionList.toString());
			throw new IllegalArgumentException(toolNotFoundExceptionList.toString() + "공기구 정보를 찾을 수 없습니다.");
		}
		
		logger.debug("SupplySheet [Add] : SupplyToolList Add Completed");

		logger.debug("SupplySheet [Add] : Completed");
	}
	
	private SupplyTool addNewSupplyTool(RentalRequestToolDto requestDto, SupplySheet sheet) {
		logger.debug("SupplyTool [Add] : Start");
		Optional<Tool> tool = toolRepository.findById(requestDto.getToolDto().getId());
		if (tool.isEmpty()){
			logger.error("tool not found");
			throw new NoSuchElementFoundException(requestDto.getToolDto().getName()+","+requestDto.getToolDto().getId()+"의 공기구 정보를 찾을 수 없습니다.");
		}
		logger.debug("SupplyTool [Add] : tool Null check completed");
		SupplyTool supplyTool = SupplyTool.builder()
				.supplySheet(sheet)
				.tool(tool.get())
				.count(requestDto.getCount())
				.replacementDate(LocalDate.now().plusMonths(tool.get().getReplacementCycle()))
				.build();
		
		try {
			StockStatus stock = stockStatusRepository.findByToolIdAndToolboxIdAndCurrentDay(requestDto.getToolDto().getId(), sheet.getToolbox().getId(), LocalDate.now());
			stockStatusService.supplyItems(stock.getId(), supplyTool.getCount());
		} catch (IllegalArgumentException e) {
			logger.error("Stock not found");
			throw new NoSuchElementFoundException(tool.get().getName() + "," + tool.get().getSpec());
		} catch (NoSuchElementFoundException e) {
			logger.error("Stock not found");
			throw new NoSuchElementFoundException(tool.get().getName() + "," + tool.get().getSpec());
		} catch (OutOfStockException e) {
			logger.error("Stock out of stock");
			throw new OutOfStockException(tool.get().getName() + "," + tool.get().getSpec());
		}
		
		logger.debug("SupplyTool [Add] : completed");
		return supplyToolRepository.save(supplyTool);
	}
	
	private OutstandingRentalSheet addNewOutstandingRentalSheet(RentalSheet sheet, List<RentalTool> toolList) {
		logger.debug("OutstandingRentalSheet [Add] : start");
		int totalCount = 0;
		for (RentalTool tool : toolList) {
			totalCount += tool.getCount();
		}
		OutstandingRentalSheet outstandingSheet = OutstandingRentalSheet.builder().rentalSheet(sheet)
				.totalCount(totalCount).totalOutstandingCount(totalCount).outstandingStatus(OutstandingState.READY)
				.build();
		logger.debug("OutstandingRentalSheet [Add] : Completed");
		return outstandingRentalSheetRepository.save(outstandingSheet);
	}
}

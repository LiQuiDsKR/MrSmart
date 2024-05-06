package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
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
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.ToolboxRepository;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestTool;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolApproveFormDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolFormDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolRepository;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolService;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheet;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetDto;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetFormDto;
import com.care4u.toolbox.stock_status.StockStatusDto;
import com.care4u.toolbox.stock_status.StockStatusService;
import com.care4u.toolbox.tag.TagDto;
import com.care4u.toolbox.tag.TagService;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.tool.ToolRepository;

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
	private final ToolRepository toolRepository;

	@Autowired
	private RentalRequestToolService rentalRequestToolService;

	@Autowired
	private TagService tagService;

	@Autowired
	private StockStatusService stockStatusService;

	@Transactional(readOnly = true)
	public List<RentalRequestSheetDto> list() {
		List<RentalRequestSheet> list = repository.findAll();

		List<RentalRequestSheetDto> dtoList = new ArrayList<RentalRequestSheetDto>();
		list.forEach(e -> dtoList.add(convertToDto(e)));
		return dtoList;
	}

	@Transactional(readOnly = true)
	public RentalRequestSheetDto get(long id) {
		Optional<RentalRequestSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}

		return convertToDto(item.get());
	}

	@Transactional(readOnly = true)
	public Page<RentalRequestSheetDto> getPageByToolbox(SheetState status, Long toolboxId, Pageable pageable) {
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxId);
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolbox : " + toolboxId);
			return null;
		}
		Page<RentalRequestSheet> page = repository.findByToolbox(status, toolbox.get(), pageable);
		return page.map(e -> convertToDto(e));
	}

	public Long getCountByToolbox(SheetState status, long toolboxId) {
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxId);
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolbox : " + toolboxId);
			return null;
		}
		long count = repository.countByToolbox(status, toolbox.get());
		return count;
	}

	@Transactional(readOnly = true)
	public Page<RentalRequestSheetDto> getPageByMembership(SheetState status, Long membershipId, Pageable pageable) {
		Optional<Membership> membership = membershipRepository.findById(membershipId);
		if (membership.isEmpty()) {
			logger.error("worker : " + membership);
			return null;
		}
		Page<RentalRequestSheet> page = repository.findByMembership(status, membership.get(), pageable);
		return page.map(e -> convertToDto(e));
	}

	@Transactional(readOnly = true)
	public Long getCountByMembership(SheetState status, Long membershipId) {
		Optional<Membership> membership = membershipRepository.findById(membershipId);
		if (membership.isEmpty()) {
			logger.error("Invalid membership : " + membershipId);
			return null;
		}
		long count = repository.countByMembership(status, membership.get());
		return count;
	}

	@Transactional(readOnly = true)
	public Page<RentalRequestSheetDto> getPage(SheetState status, long membershipId, Boolean isWorker, Boolean isLeader,
			long toolboxId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
		Optional<Membership> membershipOptional = membershipRepository.findById(membershipId);
		Optional<Toolbox> toolboxOptional = toolboxRepository.findById(toolboxId);
		Membership membership;
		if (membershipOptional.isEmpty()) {
			logger.info("no membership : " + membershipId + " all membership selected.");
			membership = null;
		} else {
			membership = membershipOptional.get();
		}
		Toolbox toolbox;
		if (toolboxOptional.isEmpty()) {
			logger.info("no toolbox : " + membershipId + " all toolbox selected.");
			toolbox = null;
		} else {
			toolbox = toolboxOptional.get();
		}
		Page<RentalRequestSheet> page = repository.findBySearchQuery(status, membership, isWorker, isLeader, toolbox,
				LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX), pageable);
		return page.map(e -> convertToDto(e));
	}

	@Transactional(readOnly = true)
	public List<RentalRequestSheetDto> getList(SheetState status, Long toolboxId) {
		List<RentalRequestSheet> list = repository.findAllByStatusAndToolboxIdOrderByEventTimestampAsc(status,
				toolboxId);
		List<RentalRequestSheetDto> dtoList = new ArrayList<RentalRequestSheetDto>();
		for (RentalRequestSheet item : list) {
			dtoList.add(convertToDto(item));
		}
		return dtoList;
	}

	@Transactional(readOnly = true)
	public List<RentalRequestSheetDto> listByTag(String tagMacAddress) {
		TagDto tagDto = tagService.get(tagMacAddress);
		List<RentalRequestTool> toolList = rentalRequestToolService.list(tagDto.getToolDto().getId());
		List<RentalRequestSheetDto> sheetList = new ArrayList<RentalRequestSheetDto>();
		for (RentalRequestTool tool : toolList) {
			sheetList.add(convertToDto(tool.getRentalRequestSheet()));
		}
		return sheetList;
	}

	@Transactional(readOnly = true)
	private RentalRequestSheetDto convertToDto(RentalRequestSheet rentalRequestSheet) {
		List<RentalRequestToolDto> dtoList = new ArrayList<RentalRequestToolDto>();
		List<RentalRequestTool> toolList = rentalRequestToolRepository
				.findAllByRentalRequestSheetId(rentalRequestSheet.getId());
		for (RentalRequestTool tool : toolList) {
			dtoList.add(new RentalRequestToolDto(tool));
		}
		return new RentalRequestSheetDto(rentalRequestSheet, dtoList);
	}

	@Transactional
	public RentalRequestSheetDto addNew(RentalRequestSheetFormDto formDto) {
		
		logger.debug("RentalRequestSheet [Add] : Start");

		// parameter null check
		Optional<Membership> worker = membershipRepository.findById(formDto.getWorkerDtoId());
		Optional<Membership> leader = membershipRepository.findById(formDto.getLeaderDtoId());
		Optional<Toolbox> toolbox = toolboxRepository.findById(formDto.getToolboxDtoId());
		if (worker.isEmpty()) {
			logger.debug("Worker not found");
			throw new IllegalArgumentException("Worker not found");
		}
		if (leader.isEmpty()) {
			logger.debug("Leader not found");
			throw new IllegalArgumentException("Leader not found");
		}
		if (toolbox.isEmpty()) {
			logger.debug("Toolbox not found");
			throw new IllegalArgumentException("Toolbox not found");
		}

		logger.debug("worker : " + worker + "\r\n" + "leader : " + leader + "\r\n" + "toolbox : " + toolbox);
		logger.debug("RentalRequestSheet [Add] : Param Null check completed");

		// sheet save
		RentalRequestSheet rentalRequestSheet = RentalRequestSheet.builder()
				.worker(worker.get())
				.leader(leader.get())
				.status(SheetState.READY)
				.toolbox(toolbox.get())
				.eventTimestamp(LocalDateTime.now())
				.build();

		RentalRequestSheet savedRentalRequestSheet = repository.save(rentalRequestSheet);
		
		logger.debug("RentalRequestSheet [Add] : RentalRequestSheet("+savedRentalRequestSheet.getId()+") saved");

		// tool save
		List<RentalRequestTool> toolList = new ArrayList<RentalRequestTool>();
		
		logger.debug("RentalRequestSheet [Add] : RentalRequestToolList Add start");
		for (RentalRequestToolFormDto tool : formDto.getToolList()) {
			RentalRequestTool newTool = rentalRequestToolService.addNew(tool, savedRentalRequestSheet);
			toolList.add(newTool);
			logger.info(newTool.getTool().getName() + " added to RentalSheet:" + savedRentalRequestSheet.getId());
		}
		logger.debug("RentalRequestSheet [Add] : RentalRequestToolList Add complete");
		
		logger.debug("RentalRequestSheet [Add] : Dto convert start");
		RentalRequestSheetDto sheetdto = convertToDto(savedRentalRequestSheet);
		logger.debug("RentalRequestSheet [Add] : Dto convert complete");
		
		return sheetdto; 
	}

	// 위를 호출
	@Transactional
	public RentalRequestSheetDto addNew(RentalRequestSheetFormDto formDto, SheetState status) {
		RentalRequestSheetDto sheetDto = addNew(formDto);
		RentalRequestSheetDto updatedSheetDto = update(sheetDto, status);
		logger.debug("RentalRequestSheet [Add] : RentalRequestSheetDto(" + sheetDto.getId() + ") updated to " + status);
		return updatedSheetDto;
	}

	// 위를 호출
	// eventTimestamp가 같을 경우, 중복 신청으로 판단. STANDBY 용.
	@Transactional
	public RentalRequestSheetDto addNew(RentalRequestSheetFormDto formDto, LocalDateTime eventTimestamp,
			SheetState status) {
		RentalRequestSheet findSheet = repository.findByEventTimestamp(eventTimestamp);
		if (findSheet != null) {
			logger.error("rentalSheet already exists! : " + eventTimestamp);
			return null;
		} else {
			Optional<RentalRequestSheet> sheetOptional = repository.findById(addNew(formDto, status).getId());
			RentalRequestSheet sheet =sheetOptional.get();
			sheet.updateEventTimestamp(eventTimestamp);
			repository.save(sheet);
			return null;
		}
	}

	@Transactional
	public RentalRequestSheetDto update(RentalRequestSheetDto sheetDto, SheetState status) {
		RentalRequestSheet rentalRequestSheet;

		Optional<RentalRequestSheet> rentalRequestSheetOptional = repository.findById(sheetDto.getId());
		Optional<Membership> worker = membershipRepository.findById(sheetDto.getWorkerDto().getId());
		Optional<Membership> leader = membershipRepository.findById(sheetDto.getLeaderDto().getId());
		Optional<Toolbox> toolbox = toolboxRepository.findById(sheetDto.getToolboxDto().getId());

		if (rentalRequestSheetOptional.isEmpty()) {
			logger.error("Sheet not found");
			throw new IllegalArgumentException("Sheet not found");
		}
		if (rentalRequestSheetOptional.get().getStatus().equals(status)) {
			logger.debug("Sheet is already " + status);
			return null;
		}

		if (worker.isEmpty()) {
			logger.error("Worker not found");
			throw new IllegalArgumentException("Worker not found");
		}

		if (leader.isEmpty()) {
			logger.error("Leader not found");
			throw new IllegalArgumentException("Leader not found");
		}

		if (toolbox.isEmpty()) {
			logger.error("Toolbox not found");
			throw new IllegalArgumentException("Toolbox not found");
		}

		rentalRequestSheet = rentalRequestSheetOptional.get();

		rentalRequestSheet.update(worker.get(), leader.get(), toolbox.get(), status, sheetDto.getEventTimestamp());

		return new RentalRequestSheetDto(repository.save(rentalRequestSheet), sheetDto.getToolList());
	}

	@Transactional
	public RentalRequestSheetDto update(RentalRequestSheetApproveFormDto formDto, SheetState status) {
		logger.debug("RentalRequestSheet [Add] : start");
		RentalRequestSheet rentalRequestSheet;
		Optional<RentalRequestSheet> rentalRequestSheetOptional = repository.findById(formDto.getId());
		Optional<Membership> worker = membershipRepository.findById(formDto.getWorkerDtoId());
		Optional<Membership> leader = membershipRepository.findById(formDto.getLeaderDtoId());
		Optional<Toolbox> toolbox = toolboxRepository.findById(formDto.getToolboxDtoId());

		if (rentalRequestSheetOptional.isEmpty()) {
			logger.error("Sheet not found");
			throw new IllegalArgumentException("Sheet not found");
		}
		if (rentalRequestSheetOptional.get().getStatus().equals(status)) {
			logger.debug("Sheet is already " + status);
			return null;
		}

		if (worker.isEmpty()) {
			logger.error("Worker not found");
			throw new IllegalArgumentException("Worker not found");
		}

		if (leader.isEmpty()) {
			logger.error("Leader not found");
			throw new IllegalArgumentException("Leader not found");
		}

		if (toolbox.isEmpty()) {
			logger.error("Toolbox not found");
			throw new IllegalArgumentException("Toolbox not found");
		}

		rentalRequestSheet = rentalRequestSheetOptional.get();

		logger.debug("RentalRequestSheet [Add] : Param Null check completed");

		rentalRequestSheet.update(worker.get(), leader.get(), toolbox.get(), status,
				rentalRequestSheet.getEventTimestamp());

		List<RentalRequestToolDto> dtoList = new ArrayList<RentalRequestToolDto>();
		logger.debug("RentalRequestSheet [Add] : toolList start");
		for (RentalRequestToolApproveFormDto tool : formDto.getToolList()) {
			logger.debug("RentalRequestSheet [Add] : tool finding");
			Optional<RentalRequestTool> optionalRequestTool = rentalRequestToolRepository.findById(tool.getId());
			Optional<Tool> optionalTool = toolRepository.findById(tool.getToolDtoId());
			if (optionalRequestTool.isEmpty()) {
				logger.error("rental request tool not found");
				throw new IllegalArgumentException("rental request tool not found");
			}
			if (optionalTool.isEmpty()) {
				logger.error("tool not found");
				throw new IllegalArgumentException("tool not found");
			}
			logger.debug("RentalRequestSheet [Add] : tool found");
			RentalRequestToolDto newDto = RentalRequestToolDto.builder().id(tool.getId()).count(tool.getCount())
					.toolDto(new ToolDto(optionalTool.get())).Tags(tool.getTags()).build();

			dtoList.add(newDto);
			logger.debug("RentalRequestSheet [Add] : tool add");
		}
		logger.debug("RentalRequestSheet [Add] : complete");
		return new RentalRequestSheetDto(repository.save(rentalRequestSheet), dtoList);
	}

	public RentalRequestSheetDto updateState(long sheetId, SheetState status) {
		Optional<RentalRequestSheet> findSheet = repository.findById(sheetId);
		if (findSheet.isEmpty()) {
			logger.error("rentalSheet not exists! : " + sheetId);
			return null;
		}
		RentalRequestSheet sheet = findSheet.get();
		sheet.updateState(status);
		return convertToDto(repository.save(sheet));
	}

	@Transactional
	public RentalRequestSheetDto cancel(long sheetId) {
		RentalRequestSheet sheet;
		Optional<RentalRequestSheet> rentalRequestSheetOptional = repository.findById(sheetId);
		if (rentalRequestSheetOptional.isEmpty()) {
			logger.error("Sheet not found");
			throw new IllegalArgumentException("Sheet not found");
		}
		sheet = rentalRequestSheetOptional.get();
		if (sheet.getStatus().equals(SheetState.CANCEL)) {
			logger.debug("Sheet already canceled!");
			return null;
		}

		sheet.update(sheet.getWorker(), sheet.getLeader(), sheet.getToolbox(), SheetState.CANCEL,
				sheet.getEventTimestamp());
		return convertToDto(repository.save(sheet));
	}
}

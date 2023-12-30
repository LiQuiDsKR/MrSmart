package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

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

import com.care4u.constant.OutstandingState;
import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipRepository;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxRepository;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheet;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolService;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheet;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetDto;
import com.care4u.toolbox.tag.Tag;
import com.care4u.toolbox.tag.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OutstandingRentalSheetService {

	private final Logger logger = LoggerFactory.getLogger(OutstandingRentalSheetService.class);

	private final OutstandingRentalSheetRepository repository;
	private final TagRepository tagRepository;
	private final MembershipRepository membershipRepository;
	private final ToolboxRepository toolboxRepository;
	private final RentalToolService rentalToolService;

	@Transactional(readOnly = true)
	public OutstandingRentalSheetDto get(long id) {
		Optional<OutstandingRentalSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		return new OutstandingRentalSheetDto(item.get(), rentalToolService.list(item.get().getRentalSheet().getId()));
	}

	@Transactional(readOnly = true)
	public OutstandingRentalSheetDto get(String macAddress) {
		Tag tag = tagRepository.findByMacaddress(macAddress);
		if (tag == null) {
			logger.error("Invalid Tag : " + macAddress);
			return null;
		} 
		if (tag.getRentalTool() == null) {
			logger.error("Tag already returned");
			return null;
		}
		long rentalSheetId = tag.getRentalTool().getRentalSheet().getId();
		
		OutstandingRentalSheet sheet = repository.findByRentalSheetId(rentalSheetId);
		if (sheet == null) {
			logger.error("Invalid Tag : " + macAddress);
			return null;
		}
		return new OutstandingRentalSheetDto(sheet, rentalToolService.list(sheet.getRentalSheet().getId()));
	}

	@Transactional(readOnly = true)
	public Page<OutstandingRentalSheetDto> getPageByToolboxId(Long toolboxId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
		Page<OutstandingRentalSheet> page = repository
				.findByOutstandingStatusAndRentalSheetToolboxIdAndRentalSheetEventTimestampBetween(OutstandingState.REQUEST, toolboxId,LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX), pageable);
		return page.map(e -> new OutstandingRentalSheetDto(e, rentalToolService.list(e.getRentalSheet().getId())));
	}
	/**
	 * @deprecated 앱에서만 쓰는데 date쿼리 필요없어져서 변경. & readyㅋㅋ
	 */
	@Transactional(readOnly = true)
	public List<OutstandingRentalSheetDto> getListByToolboxId(Long toolboxId, LocalDate startDate, LocalDate endDate) {
		List<OutstandingRentalSheet> list = repository
				.findByOutstandingStatusAndRentalSheetToolboxIdAndRentalSheetEventTimestampBetween(OutstandingState.REQUEST, toolboxId,LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX));
		List<OutstandingRentalSheetDto> dtoList = new ArrayList<OutstandingRentalSheetDto>();
		for (OutstandingRentalSheet item : list) {
			dtoList.add(new OutstandingRentalSheetDto(item, rentalToolService.list(item.getRentalSheet().getId())));
		}
		return dtoList;
	}
	@Transactional(readOnly = true)
	public List<OutstandingRentalSheetDto> getListByToolboxId(Long toolboxId) {
		List<OutstandingRentalSheet> list = repository
			.findByOutstandingStatusAndRentalSheetToolboxId(OutstandingState.REQUEST, toolboxId);
		List<OutstandingRentalSheetDto> dtoList = new ArrayList<OutstandingRentalSheetDto>();
		for (OutstandingRentalSheet item : list) {
			dtoList.add(new OutstandingRentalSheetDto(item, rentalToolService.list(item.getRentalSheet().getId())));
		}
		return dtoList;
	}
	@Transactional(readOnly = true)
	public Page<OutstandingRentalSheetDto> getPageByMembershipId(Long membershipId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
		Page<OutstandingRentalSheet> page = repository
				.findByRentalSheetMembershipIdAndRentalSheetEventTimestampBetween(membershipId,LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX), pageable);
		return page.map(e -> new OutstandingRentalSheetDto(e, rentalToolService.list(e.getRentalSheet().getId())));
	}
	/**
	 * @deprecated 앱에서만 쓰는데 date쿼리 필요없어져서 변경. & readyㅋㅋ
	 */
	@Transactional(readOnly = true)
	public List<OutstandingRentalSheetDto> getListByMembershipId(Long membershipId, LocalDate startDate, LocalDate endDate) {
		List<OutstandingRentalSheet> list = repository
				.findByRentalSheetMembershipIdAndRentalSheetEventTimestampBetween(membershipId,LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX));
		List<OutstandingRentalSheetDto> dtoList = new ArrayList<OutstandingRentalSheetDto>();
		for (OutstandingRentalSheet item : list) {
			dtoList.add(new OutstandingRentalSheetDto(item, rentalToolService.list(item.getRentalSheet().getId())));
		}
		return dtoList;
	}
	@Transactional(readOnly = true)
	public List<OutstandingRentalSheetDto> getListByMembershipId(Long membershipId) {
		List<OutstandingRentalSheet> list = repository
				.findByOutstandingStatusAndLeaderIdOrWorkerIdOrApproverId(OutstandingState.READY,membershipId);
		List<OutstandingRentalSheetDto> dtoList = new ArrayList<OutstandingRentalSheetDto>();
		for (OutstandingRentalSheet item : list) {
			dtoList.add(new OutstandingRentalSheetDto(item, rentalToolService.list(item.getRentalSheet().getId())));
		}
		return dtoList;
	}
	
	
	@Transactional
	public OutstandingRentalSheet addNew(RentalSheet sheet, List<RentalTool> toolList) {
		int totalCount = 0;
		for (RentalTool tool : toolList) {
			totalCount += tool.getCount();
		}
		OutstandingRentalSheet outstandingSheet = OutstandingRentalSheet.builder()
				.rentalSheet(sheet)
				.totalCount(totalCount)
				.totalOutstandingCount(totalCount)
				.outstandingStatus(OutstandingState.READY)
				.build();

		return repository.save(outstandingSheet);
	}
	
	
	//반납 승인 후
	@Transactional
	public OutstandingRentalSheetDto update(ReturnSheetDto returnSheetDto) {
		OutstandingRentalSheet sheet = repository.findByRentalSheetId(returnSheetDto.getRentalSheetDto().getId());
		if (sheet == null) {
			logger.error("outstandingSheet not found!");
			return null;
		}
		
		//outstandingCount == 0 ? delete from pool
		List<RentalToolDto> toolList = returnSheetDto.getRentalSheetDto().getToolList();
		int totalOutstandingCount = 0;
		for (RentalToolDto tool : toolList) {
			totalOutstandingCount += tool.getOutstandingCount();
		}
		sheet.updateOutstandingState(OutstandingState.READY);
		
		if (totalOutstandingCount == 0) {
			repository.deleteById(sheet.getId());
			return null;
		}else {
			sheet.updateOutstandingCount(totalOutstandingCount);
			return new OutstandingRentalSheetDto(repository.save(sheet), toolList);
		}
	}
	
	
	//반납 신청 시 outstandingState를 Request로 바꿉니다.
	@Transactional
	public String requestOutstandingState(long id) {
		Optional<OutstandingRentalSheet> sheetOptional = repository.findById(id);
		if (sheetOptional.isEmpty()) {
			logger.error("outstandingSheet not found!");
			return null;
		}
		OutstandingRentalSheet sheet = sheetOptional.get();
		sheet.updateOutstandingState(OutstandingState.REQUEST);
		repository.save(sheet);
		
		return "outstandingRentalSheet Id : "+sheet.getId()+" OutstandingStatus changed to" + sheet.getOutstandingStatus();
	}

	public Page<OutstandingRentalSheetDto> getPage(OutstandingState status, long membershipId, Boolean isWorker,
			Boolean isLeader, long toolboxId, LocalDate startLocalDate, LocalDate endLocalDate, Pageable pageable) {
		Optional<Membership> membership = membershipRepository.findById(membershipId);
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxId);
		if (membership.isEmpty()) {
			logger.error("worker : " + membership);
			return null;
		}
		if (toolbox.isEmpty()) {
			logger.error("toolbox : " + toolbox);
			return null;
		}

		List<OutstandingRentalSheetDto> dtoList = new ArrayList<OutstandingRentalSheetDto>();
		Page<OutstandingRentalSheet> page= repository.findBySearchQuery(status, membership.get(), isWorker, isLeader, toolbox.get(),LocalDateTime.of(startLocalDate, LocalTime.MIN), LocalDateTime.of(endLocalDate, LocalTime.MAX), pageable);	
		Page<OutstandingRentalSheet> page = repository
				.findByRentalSheetMembershipIdAndRentalSheetEventTimestampBetween(membershipId,LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX), pageable);
		return page.map(e -> new OutstandingRentalSheetDto(e, rentalToolService.list(e.getRentalSheet().getId())));
	}
}

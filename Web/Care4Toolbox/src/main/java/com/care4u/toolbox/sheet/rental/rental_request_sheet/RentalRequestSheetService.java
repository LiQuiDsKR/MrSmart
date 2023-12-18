package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDateTime;
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
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolFormDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolRepository;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolService;
import com.care4u.toolbox.tag.TagDto;
import com.care4u.toolbox.tag.TagService;

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
	private RentalRequestToolService rentalRequestToolService;
	
	@Autowired
	private TagService tagService;
	
	
	@Transactional(readOnly = true)
	public List<RentalRequestSheetDto> list(){
		List<RentalRequestSheet> list = repository.findAll();
		
		List<RentalRequestSheetDto> dtoList = new ArrayList<RentalRequestSheetDto>();
		list.forEach(e->dtoList.add(convertToDto(e)));
		return dtoList;
	}
	
	@Transactional(readOnly = true)
	public RentalRequestSheetDto get(long id){
		Optional<RentalRequestSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return convertToDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public Page<RentalRequestSheetDto> getPage( Long toolboxId, Pageable pageable ) {
		Page<RentalRequestSheet> page = repository.findAllByToolboxId(toolboxId, pageable);	
		return page.map(e->convertToDto(e));
	}

	@Transactional(readOnly=true)
	public Page<RentalRequestSheetDto> getPage(SheetState status, Long toolboxId, Pageable pageable){
		Page<RentalRequestSheet> page= repository.findAllByStatusAndToolboxIdOrderByEventTimestampAsc(status, toolboxId, pageable);	
		return page.map(e->convertToDto(e));
	}
	
	@Transactional(readOnly = true)
	public List<RentalRequestSheetDto> getList(SheetState status, Long toolboxId) {
		List<RentalRequestSheet> list = repository.findAllByStatusAndToolboxIdOrderByEventTimestampAsc(status, toolboxId);
		List<RentalRequestSheetDto> dtoList = new ArrayList<RentalRequestSheetDto>();
		for (RentalRequestSheet item : list) {
			dtoList.add(convertToDto(item));
		}
		return dtoList;
	}
	
	
	@Transactional(readOnly = true)
	public List<RentalRequestSheetDto> listByTag(String tagMacAddress){
		TagDto tagDto = tagService.get(tagMacAddress);
		List<RentalRequestTool> toolList = rentalRequestToolService.list(tagDto.getToolDto().getId());
		List<RentalRequestSheetDto> sheetList = new ArrayList<RentalRequestSheetDto>();
		for (RentalRequestTool tool : toolList) {
			sheetList.add(convertToDto(tool.getRentalRequestSheet()));
		}
		return sheetList;
	}
	@Transactional(readOnly=true)
	private RentalRequestSheetDto convertToDto(RentalRequestSheet rentalRequestSheet) {
		List<RentalRequestToolDto> dtoList = new ArrayList<RentalRequestToolDto>();
		List<RentalRequestTool> toolList = rentalRequestToolRepository.findAllByRentalRequestSheetId(rentalRequestSheet.getId());
		for (RentalRequestTool tool : toolList) {
			dtoList.add(new RentalRequestToolDto(tool));
		}
		return new RentalRequestSheetDto(rentalRequestSheet,dtoList);
	}

	@Transactional
	public RentalRequestSheetDto addNew(RentalRequestSheetFormDto formDto) {
		
		Optional<Membership> worker = membershipRepository.findById(formDto.getWorkerDtoId());
		Optional<Membership> leader = membershipRepository.findById(formDto.getLeaderDtoId());
		Optional<Toolbox> toolbox = toolboxRepository.findById(formDto.getToolboxDtoId());
		if (worker.isEmpty()) {
			logger.debug("worker : " + worker);
			return null;
		}
		if (leader.isEmpty()) {
			logger.debug("leader :" + leader);
			return null;
		}
		if (toolbox.isEmpty()) {
			logger.debug("toolbox : " + toolbox);
			return null;
		}

		logger.debug("worker : " + worker + "\r\n" + "leader : " + leader + "\r\n" + "toolbox : " + toolbox);
		
		RentalRequestSheet rentalRequestSheet = RentalRequestSheet.builder()
			.worker(worker.get())
			.leader(leader.get())
			.status(SheetState.REQUEST)
			.toolbox(toolbox.get())
			.eventTimestamp(LocalDateTime.now())
			.build();
		
		RentalRequestSheet savedRentalRequestSheet = repository.save(rentalRequestSheet);
		
		List<RentalRequestTool> toolList = new ArrayList<RentalRequestTool>();
		for (RentalRequestToolFormDto tool : formDto.getToolList()) {
			RentalRequestTool newTool = rentalRequestToolService.addNew(tool, savedRentalRequestSheet);
			toolList.add(newTool);
		}
		return convertToDto(savedRentalRequestSheet);
	}
	
	
	@Transactional
	public RentalRequestSheetDto update(RentalRequestSheetDto sheetDto) {
		RentalRequestSheet rentalRequestSheet;
		
		Optional<RentalRequestSheet> rentalRequestSheetOptional = repository.findById(sheetDto.getId());
		Optional<Membership> worker = membershipRepository.findById(sheetDto.getWorkerDto().getId());
		Optional<Membership> leader = membershipRepository.findById(sheetDto.getLeaderDto().getId());
		Optional<Toolbox> toolbox = toolboxRepository.findById(sheetDto.getToolboxDto().getId());

	    if (rentalRequestSheetOptional.isEmpty()) {
	        logger.error("Sheet not found");
	        throw new IllegalArgumentException("Sheet not found");
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
		
		rentalRequestSheet=rentalRequestSheetOptional.get();
		rentalRequestSheet.update(worker.get(), leader.get(), toolbox.get(), sheetDto.getStatus(), sheetDto.getEventTimestamp());
		
		return new RentalRequestSheetDto(repository.save(rentalRequestSheet), sheetDto.getToolList());
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
		
		rentalRequestSheet=rentalRequestSheetOptional.get();
		rentalRequestSheet.update(worker.get(), leader.get(), toolbox.get(), status, sheetDto.getEventTimestamp());
		
		return new RentalRequestSheetDto(repository.save(rentalRequestSheet), sheetDto.getToolList());
	}
	@Transactional
	public RentalRequestSheetDto cancel(long sheetId) {
		RentalRequestSheet sheet;
		Optional<RentalRequestSheet> rentalRequestSheetOptional = repository.findById(sheetId);
		if (rentalRequestSheetOptional.isEmpty()) {
	        logger.error("Sheet not found");
	        throw new IllegalArgumentException("Sheet not found");
	    }
		sheet=rentalRequestSheetOptional.get();
		sheet.update(sheet.getWorker(), sheet.getLeader(), sheet.getToolbox(), SheetState.CANCEL, sheet.getEventTimestamp());
		return convertToDto(repository.save(sheet));
	}
}

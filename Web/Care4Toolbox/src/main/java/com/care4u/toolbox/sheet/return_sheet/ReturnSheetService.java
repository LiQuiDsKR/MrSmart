package com.care4u.toolbox.sheet.return_sheet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipRepository;
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
		Optional<RentalSheet> rentalSheet = rentalSheetRepository.findById(formDto.getRentalSheetId());
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
}

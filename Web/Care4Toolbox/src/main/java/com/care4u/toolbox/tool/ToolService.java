package com.care4u.toolbox.tool;

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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.hr.sub_part.SubPartService;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.group.sub_group.SubGroup;
import com.care4u.toolbox.group.sub_group.SubGroupRepository;
import com.care4u.toolbox.stock_status.StockStatus;
import com.care4u.toolbox.stock_status.StockStatusDto;
import com.care4u.toolbox.stock_status.StockStatusRepository;
import com.care4u.toolbox.stock_status.StockStatusService;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabel;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelDto;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelRepository;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ToolService {

	private final Logger logger = LoggerFactory.getLogger(SubPartService.class);
	
	private final ToolRepository repository;
	private final SubGroupRepository subGroupRepository;
	private final StockStatusRepository stockStatusRepository;
	private final ToolboxToolLabelRepository toolboxToolLabelRepository;
	
	private final StockStatusService stockStatusService;
	private final ToolboxToolLabelService toolboxToolLabelService;
	
	@Transactional(readOnly = true)
	public ToolDto get(long id){
		Optional<Tool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new ToolDto(item.get());
	}	
	
	@Transactional(readOnly = true)
	public ToolDto getByCode(String code){
		Tool item = repository.findByCode(code);
		if (item == null) {
			logger.error("Invalid code : " + code);
			return null;
		}
		
		return new ToolDto(item);
	}
	
	@Transactional(readOnly = true)
	public List<ToolDto> list(){
		List<Tool> list = repository.findAllByOrderByNameAsc();
		return getDtoList(list);
	}
	
	@Transactional(readOnly = true)
	public List<ToolDto> listBySubGroupId(long subGroupId){
		List<Tool> list = repository.findAllBySubGroupIdOrderByNameAsc(subGroupId);
		return getDtoList(list);
	}
	
	public ToolDto addNew(ToolDto toolDto) throws IllegalStateException {
		Tool findItem = repository.findByCode(toolDto.getCode());
		if(findItem != null){
			logger.error("이미 등록된 코드입니다. : " + toolDto.getCode());
			throw new IllegalStateException("이미 등록된 코드입니다.");
		}
		return update(toolDto.getSubGroupDto().getId(), toolDto);
	}
	
	public ToolDto update(long subGroupId, ToolDto toolDto) throws IllegalStateException {
		Optional<SubGroup> subGroup = subGroupRepository.findById(subGroupId);
		if (subGroup.isEmpty()) {
			throw new IllegalStateException("등록되지 않은 중분류 코드입니다.");
		}
		
		Tool tool = repository.findByCode(toolDto.getCode());
		if (tool == null) {
			tool = new Tool(toolDto.getCode());
		}
		tool.update(toolDto, subGroup.get());
		
		return new ToolDto(repository.save(tool));
	}
	
	private List<ToolDto> getDtoList(List<Tool> list){
		List<ToolDto> dtoList = new ArrayList<ToolDto>();
		for (Tool item : list) {
			dtoList.add(new ToolDto(item));
			
		}
		return dtoList;
	}
	
	/**
	 * 밑에 Name & subGroupId로 쓰세요
	 * @deprecated
	 * @param pageable
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<ToolDto> getToolPage(Pageable pageable, String name){
		Page<Tool> membershipPage = repository.findByNameContaining(pageable, name);
		return membershipPage.map(ToolDto::new);
	}
	
	@Transactional(readOnly = true)
	public Page<ToolDto> getToolPage(Pageable pageable, String name, List<Long> subGroupId){
		Page<Tool> toolPage = repository.findByNameContainingAndSubGroupIdIn(pageable,name,subGroupId);
		logger.info("tool total page : " + toolPage.getTotalPages() + ", current page : " + toolPage.getNumber());
		return toolPage.map(ToolDto::new);
	}
	

	/**
	 * StockStatus를 기준으로, ToolboxId에 대한 쿼리 실행 후 ToolForRentalDtoPage를 반환
	 * @param pageable
	 * @param toolboxId
	 * @return
	 */
	@Transactional
	public Page<ToolForRentalDto> getToolForRentalDtoPage(Pageable pageable, long toolboxId, String name, List<Long> subGroupId){
		LocalDate date = LocalDate.now();
		Page<StockStatus> stockPage = stockStatusRepository.findAllByToolboxIdAndCurrentDay(toolboxId, date, name, subGroupId, pageable);
		List<ToolForRentalDto> list = new ArrayList<ToolForRentalDto>();
		for (StockStatus stock : stockPage) {
			Tool tool = stock.getTool();
			Toolbox toolbox = stock.getToolbox();
			ToolboxToolLabel label = toolboxToolLabelRepository.findByToolIdAndToolboxId(tool.getId(), toolbox.getId());
			if (label==null) {
				logger.error("no label");
			}
			ToolDto toolDto = new ToolDto(tool);
			ToolboxToolLabelDto labelDto = new ToolboxToolLabelDto(label);
			StockStatusDto stockDto = new StockStatusDto(stock);
			ToolForRentalDto toolForRentalDto = ToolForRentalDto.builder()
					.toolDto(toolDto)
					.labelDto(labelDto)
					.stockDto(stockDto)
					.build();
			list.add(toolForRentalDto);
		}
		return new PageImpl<ToolForRentalDto>(list, PageRequest.of(stockPage.getNumber(), stockPage.getSize()), stockPage.getTotalElements());
	}
}

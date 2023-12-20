package com.care4u.toolbox.toolbox_tool_label;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.ToolboxRepository;
import com.care4u.toolbox.ToolboxService;
import com.care4u.toolbox.stock_status.StockStatusService;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.tool.ToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ToolboxToolLabelService {

	private final Logger logger = LoggerFactory.getLogger(ToolboxToolLabelService.class);
	
	private final ToolboxToolLabelRepository repository;
	private final ToolboxRepository toolboxRepository;
	private final ToolRepository toolRepository;
	private final ToolboxService toolboxService;
	private final StockStatusService stockStatusService;
	
	@Transactional(readOnly = true)
	public ToolboxToolLabelDto get(long id){
		Optional<ToolboxToolLabel> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new ToolboxToolLabelDto(item.get());
	}	
	
	@Transactional(readOnly = true)
	public ToolboxToolLabelDto get(ToolboxDto toolboxDto, String location){
		Toolbox toolbox = toolboxRepository.findByName(toolboxDto.getName());
		if (toolbox == null) {
			logger.error("Invalid toolboxDto : " + toolboxDto.getName());
			return null;
		}
		
		ToolboxToolLabel item = repository.findByToolboxAndLocation(toolbox, location);
		if (item == null) {
			logger.error("Invalid location : " + location);
			return null;
		}
		
		return new ToolboxToolLabelDto(item);
	}

	@Transactional(readOnly = true)
	public ToolboxToolLabelDto get(long toolId, long toolboxId){
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxId);
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolboxId : " + toolboxId);
			return null;
		}
		
		Optional<Tool> tool = toolRepository.findById(toolId);		
		if (tool.isEmpty()) {
			logger.error("Invalid toolId : " + toolId);
			return null;
		}
		
		ToolboxToolLabel label = repository.findByToolIdAndToolboxId(toolId, toolboxId);
		if (label == null) {
			return null;
		}
		return new ToolboxToolLabelDto(label);
	}
	
	@Transactional(readOnly = true)
	public List<ToolboxToolLabelDto> listByToolboxId(long toolboxId){
		List<ToolboxToolLabelDto> list = new ArrayList<ToolboxToolLabelDto>();
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxId);
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolbox : " + toolboxId);
			return list;
		}
		List<ToolboxToolLabel> subGroupList = repository.findAllByToolboxOrderByLocationAsc(toolbox.get());
		return getDtoList(subGroupList);
	}
	
	@Transactional
	public ToolboxToolLabelDto update(ToolboxToolLabelDto toolboxToolLabelDto) {
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxToolLabelDto.getToolboxDto().getId());
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolboxId : " + toolboxToolLabelDto.getToolboxDto().getId());
			return null;
		}
		
		Optional<Tool> tool = toolRepository.findById(toolboxToolLabelDto.getToolDto().getId());		
		if (tool.isEmpty()) {
			logger.error("Invalid toolId : " + toolboxToolLabelDto.getToolDto().getId());
			return null;
		}
		ToolboxToolLabel toolboxToolLabel = repository.findByToolIdAndToolboxId(toolboxToolLabelDto.getToolDto().getId(), toolboxToolLabelDto.getToolboxDto().getId());
		if(toolboxToolLabel == null){
			return addNew(toolboxToolLabelDto.getToolDto().getId(), toolboxToolLabelDto.getToolboxDto().getId(), toolboxToolLabelDto.getQrcode());
		}
		
		toolboxToolLabel.update(toolbox.get(), toolboxToolLabelDto.getLocation(), tool.get(), toolboxToolLabelDto.getQrcode());
		
		return new ToolboxToolLabelDto(repository.save(toolboxToolLabel));
	}
	/**
	 * @deprecated 어짜피 register에서만 쓰니까 오버로드된 다른 버전 쓰세요.
	 * @param toolId
	 * @param toolboxId
	 * @param qrcode
	 * @return
	 */
	@Transactional
	public ToolboxToolLabelDto update(long toolId, long toolboxId, String qrcode) {

		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxId);
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolboxId : " + toolboxId);
			return null;
		}
		
		Optional<Tool> tool = toolRepository.findById(toolId);		
		if (tool.isEmpty()) {
			logger.error("Invalid toolId : " + toolId);
			return null;
		}
		
		ToolboxToolLabel toolboxToolLabel = repository.findByToolIdAndToolboxId(toolId, toolboxId);
		if(toolboxToolLabel == null){
			return addNew(toolId, toolboxId, qrcode);
		}
		
		toolboxToolLabel.update(toolbox.get(), toolboxToolLabel.getLocation(), tool.get(), qrcode);
		
		return new ToolboxToolLabelDto(repository.save(toolboxToolLabel));
	}
	@Transactional
	public ToolboxToolLabelDto update(ToolboxToolLabel label, String qrcode) {
		label.update(label.getToolbox(), label.getLocation(), label.getTool(), qrcode);
		return new ToolboxToolLabelDto(repository.save(label));
	}
	
	@Transactional
	public ToolboxToolLabelDto register(long toolId, long toolboxId, String qrcode) {
		ToolboxToolLabel tempObject = repository.findByQrcode(qrcode);
		ToolboxToolLabel toolboxToolLabel = repository.findByToolIdAndToolboxId(toolId, toolboxId);
		if(toolboxToolLabel!=null) {
			if (tempObject.equals(toolboxToolLabel)) {
				return update(toolboxToolLabel,qrcode);	
			}else {
				logger.error(qrcode +" already exists!");
				throw new IllegalArgumentException(qrcode +" already exists!");
			}
		}
		return addNew(toolId,toolboxId,qrcode); 
	}
	
	@Transactional
	public ToolboxToolLabelDto addNew(long toolId, long toolboxId, String qrcode) {		
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxId);
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolboxId : " + toolboxId);
			return null;
		}
		
		Optional<Tool> tool = toolRepository.findById(toolId);
		if (tool.isEmpty()) {
			logger.error("Invalid toolId : " + toolId);
			return null;
		}
		
		ToolboxToolLabel label = ToolboxToolLabel.builder()
				.tool(tool.get())
				.toolbox(toolbox.get())
				.location("")
				.qrcode(qrcode)
				.build();
		
		repository.save(label);
		
		return new ToolboxToolLabelDto(label);
	}
	
	
	/**
	 * tool에 대해 임의의 toolbox를 연결하는 toolboxToolLabelDto를 생성합니다
	 * location="", qrcode=tool.code
	 * @param toolDto
	 * @return new toolboxToolLabelDto
	 */
	private ToolboxToolLabelDto addDummy(ToolDto toolDto) {
		
		Optional<Tool> tool = toolRepository.findById(toolDto.getId());		
		if (tool.isEmpty()) {
			logger.error("Invalid toolId : " + toolDto.getId());
			return null;
		}
		
		List<ToolboxDto> toolboxList = toolboxService.list();
		Random random = new Random();
		int randomIndex = random.nextInt(toolboxList.size());
		ToolboxDto toolboxDto = toolboxList.get(randomIndex);	
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxDto.getId());
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolboxId : " + toolboxDto.getId());
			return null;
		}
		
		ToolboxToolLabel toolboxToolLabel = repository.findByToolIdAndToolboxId(toolDto.getId(),toolboxDto.getId());
		if (toolboxToolLabel == null) {
			toolboxToolLabel = new ToolboxToolLabel();
			toolboxToolLabel.update(toolbox.get(),"",tool.get(),toolDto.getCode());
		} else {
			return new ToolboxToolLabelDto(toolboxToolLabel);
		}
		return new ToolboxToolLabelDto(repository.save(toolboxToolLabel));
	}
	
	
	private List<ToolboxToolLabelDto> getDtoList(List<ToolboxToolLabel> list){
		List<ToolboxToolLabelDto> dtoList = new ArrayList<ToolboxToolLabelDto>();
		for (ToolboxToolLabel item : list) {
			dtoList.add(new ToolboxToolLabelDto(item));
		}
		return dtoList;
	}

}
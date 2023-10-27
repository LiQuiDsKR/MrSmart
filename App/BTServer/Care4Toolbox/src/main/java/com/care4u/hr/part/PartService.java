package com.care4u.hr.part;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.hr.sub_part.SubPart;
import com.care4u.hr.sub_part.SubPartRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PartService {

	private final Logger logger = LoggerFactory.getLogger(PartService.class);
	
	private final PartRepository repository;
	private final SubPartRepository subPartRepository;
	
	@Transactional(readOnly = true)
	public PartDto get(long id){
		Optional<Part> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new PartDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public PartDto get(long subPartId, String name){
		Part item = repository.findBySubPartIdAndName(subPartId, name);
		if (item == null) {
			return null;
		}
		
		return new PartDto(item);
	}
	
	@Transactional(readOnly = true)
	public List<PartDto> list(){
		List<Part> list = repository.findAllByOrderByNameAsc();
		return getDtoList(list);
	}
	
	@Transactional(readOnly = true)
	public List<PartDto> listBySubPartId(long subPartId){
		List<PartDto> list = new ArrayList<PartDto>();
		Optional<SubPart> subPart = subPartRepository.findById(subPartId);
		if (subPart.isEmpty()) {
			logger.error("Invalid subPartId : " + subPartId);
			return list;
		}
		List<Part> subGroupList = repository.findAllBySubPartOrderByNameAsc(subPart.get());
		return getDtoList(subGroupList);
	}
	
	public PartDto addNew(long subPartId, PartDto partDto) throws IllegalStateException {
		Part findItem = repository.findBySubPartIdAndName(subPartId, partDto.getName());
		if(findItem != null){
			logger.error("이미 등옥된 이름입니다. : " + partDto.getName());
			throw new IllegalStateException("이미 등옥된 이름입니다.");
		}
		return update(subPartId, partDto);
	}
	
	public PartDto update(long subPartId, PartDto partDto) throws IllegalStateException {
		Optional<SubPart> subPart = subPartRepository.findById(subPartId);
		if (subPart.isEmpty()) {
			logger.error("Invalid subPartId : " + subPartId);
			throw new IllegalStateException("등록되지 않은 그룹입니다.");
		}
		
		Part part = repository.findBySubPartIdAndName(subPartId, partDto.getName());
		if (part == null) {
			part = new Part();
		}		
		part.update(subPart.get(), partDto);
		
		return new PartDto(repository.save(part));
	}
	
	private List<PartDto> getDtoList(List<Part> list){
		List<PartDto> dtoList = new ArrayList<PartDto>();
		for (Part item : list) {
			dtoList.add(new PartDto(item));
			
		}
		return dtoList;
	}

}

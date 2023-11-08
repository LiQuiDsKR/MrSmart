package com.care4u.hr.sub_part;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.hr.main_part.MainPart;
import com.care4u.hr.main_part.MainPartRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SubPartService {

	private final Logger logger = LoggerFactory.getLogger(SubPartService.class);
	
	private final SubPartRepository repository;
	private final MainPartRepository mainPartRepository;
	
	@Transactional(readOnly = true)
	public SubPartDto get(long id){
		Optional<SubPart> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}		
		return new SubPartDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public SubPartDto get(long mainPartId, String name){
		SubPart item = repository.findByMainPartIdAndName(mainPartId, name);
		if (item == null) {
			return null;
		}
		
		return new SubPartDto(item);
	}
	
	@Transactional(readOnly = true)
	public List<SubPartDto> list(){
		List<SubPart> list = repository.findAllByOrderByNameAsc();
		return getDtoList(list);
	}
	
	@Transactional(readOnly = true)
	public List<SubPartDto> listByMainPartId(long mainPartId){
		List<SubPartDto> list = new ArrayList<SubPartDto>();
		Optional<MainPart> mainPart = mainPartRepository.findById(mainPartId);
		if (mainPart.isEmpty()) {
			logger.error("Invalid mainPartId : " + mainPartId);
			return list;
		}
		List<SubPart> subPartList = repository.findAllByMainPartOrderByNameAsc(mainPart.get());
		return getDtoList(subPartList);
	}
	
	public SubPartDto addNew(long mainPartId, SubPartDto subPartDto) throws IllegalStateException {
		SubPart findItem = repository.findByMainPartIdAndName(mainPartId, subPartDto.getName());
		if(findItem != null){
			logger.error("이미 등옥된 이름입니다. : " + subPartDto.getName());
			throw new IllegalStateException("이미 등옥된 이름입니다.");
		}
		return update(mainPartId, subPartDto);
	}
	
	public SubPartDto update(long mainPartId, SubPartDto subPartDto) throws IllegalStateException {
		Optional<MainPart> mainPart = mainPartRepository.findById(mainPartId);
		if (mainPart.isEmpty()) {
			logger.error("Invalid mainPartId : " + mainPartId);
			throw new IllegalStateException("등록되지 않은 정비실입니다.");
		}
		
		SubPart subPart = repository.findByMainPartIdAndName(mainPartId, subPartDto.getName());
		if (subPart == null) {
			subPart = new SubPart();			
		}
		subPart.update(mainPart.get(), subPartDto);
		
		return new SubPartDto(repository.save(subPart));
	}
	
	public SubPartDto update(SubPartDto subPartDto) throws IllegalStateException {
		Optional<MainPart> mainPart = mainPartRepository.findById(subPartDto.getMainPartDto().getId());
		if (mainPart.isEmpty()) {
			logger.error("Invalid mainPartId : " + subPartDto.getMainPartDto().getId());
			throw new IllegalStateException("등록되지 않은 정비실입니다.");
		}
		
		SubPart subPart = repository.findById(subPartDto.getId()).get();
		if (subPart == null) {
			subPart = new SubPart();			
		}
		subPart.update(mainPart.get(), subPartDto);
		
		return new SubPartDto(repository.save(subPart));
	}
	
	private List<SubPartDto> getDtoList(List<SubPart> list){
		List<SubPartDto> dtoList = new ArrayList<SubPartDto>();
		for (SubPart item : list) {
			dtoList.add(new SubPartDto(item));
			
		}
		return dtoList;
	}

}

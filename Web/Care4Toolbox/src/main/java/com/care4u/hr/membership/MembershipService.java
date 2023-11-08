package com.care4u.hr.membership;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.constant.Role;
import com.care4u.hr.part.Part;
import com.care4u.hr.part.PartRepository;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;

@Service
@Transactional
@RequiredArgsConstructor
public class MembershipService {
	
private final Logger logger = LoggerFactory.getLogger(MembershipService.class);

	private final MembershipRepository repository;
	private final PartRepository partRepository;

	public void addNew(Membership item) {
		
	}
	
	public MembershipDto addNew(MembershipDto memberDto) throws IllegalStateException {
		Membership findMember = repository.findByCode(memberDto.getCode());
		if(findMember != null){
			logger.error("이미 등록된 코드입니다. : " + memberDto.getName());
			throw new IllegalStateException("이미 가입된 사원번호입니다.");
		}
		return update(memberDto.getPartDto().getId(), memberDto);
	}

	public MembershipDto update(long partId, MembershipDto membershipDto) throws IllegalStateException {
		Optional<Part> part = partRepository.findById(partId);
		if (part.isEmpty()) {
			logger.error("Invalid partId : " + partId);
			throw new IllegalStateException("등록되지 않은 파트입니다.");
		}
	
		Membership membership = repository.findByCode(membershipDto.getCode());
		if (membership == null) {
			membership = new Membership(membershipDto.getCode());
		}
		membership.update(part.get(), membershipDto);

		return new MembershipDto(repository.save(membership));
	}

	public Membership loadUserByCode(String code) {
		Membership member = repository.findByCode(code);                        
		return member;
	}
	
	/**
     * 2023-10-19 박경수 추가
     * MembershipDto list 뽑아가려고 했는데 메서드가 만들어진 게 없어서 만들어봤습니다
     */
    @Transactional(readOnly = true)
	public List<MembershipDto> list(){
		List<Membership> list = repository.findAllByRoleOrderByNameAsc(Role.USER);
		return getDtoList(list);
	}
	
	private List<MembershipDto> getDtoList(List<Membership> list){
		List<MembershipDto> dtoList = new ArrayList<MembershipDto>();
		for (Membership item : list) {
			dtoList.add(new MembershipDto(item));
		}
		return dtoList;
	}
	
	/**
	 * 2023-10-25 박경수
	 * search & paging 기능 테스트를 위해 추가했습니다
	 * 2023-10-31 박경수
	 * return을 Page<dto>로 변환했습니다
	 */
	@Transactional(readOnly = true)
    public Page<MembershipDto> getMembershipPage(MembershipSearchDto membershipSearchDto, Pageable pageable){
		Page<Membership> membershipPage = repository.findAll(pageable);
		logger.info("membership total page : " + membershipPage.getTotalPages() + ", current page : " + membershipPage.getNumber());
		return membershipPage.map(MembershipDto::new);
    }
	
	@Transactional(readOnly = true)
	public Page<MembershipDto> getMembershipPageByName(Pageable pageable, String name){
		Page<Membership> membershipPage = repository.findByNameContaining(pageable, name);
		return membershipPage.map(MembershipDto::new);
	}
}
package com.care4u.manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.hr.membership.MembershipService;
import com.care4u.hr.part.PartDto;
import com.care4u.hr.part.PartService;
import com.care4u.hr.sub_part.SubPartDto;
import com.care4u.hr.sub_part.SubPartService;

public class MemberParsing {
	private final Logger logger = Logger.getLogger(MemberParsing.class);
	
	private final MainPartService mainPartService;
	private final SubPartService subPartService;
	private final PartService partService;
	private final MembershipService membershipService;
	
	public MemberParsing(MainPartService mainPartService, SubPartService subPartService, PartService partService, MembershipService membershipService) {
		this.mainPartService = mainPartService;
		this.subPartService = subPartService;
		this.partService = partService;
		this.membershipService = membershipService;
	}
	
	public void readCsvFile(String csvFilePath) {
		
		try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
			String line;
			
			// 라인 단위로 읽어오기
			int count = 1;
			while ((line = reader.readLine()) != null) {
				// ','로 구분된 데이터 파싱
				String[] datas = line.split(",");
				
				if (datas.length == 4) {
					// CSV 데이터를 Member 객체로 매핑
					PartDto partDto = parseDepartment(datas[1].trim());
					if (partDto == null) return;
					
					MembershipDto memberDto = MembershipDto.builder()
									.name(datas[2].trim())
									.code(datas[3].trim())
									.password("123")
									.role(Role.USER)
									.employmentState(EmploymentState.EMPLOYMENT)
									.partDto(partDto)
									.build();
					logger.info(count + " : " + memberDto.toString());                	
					count++;
					membershipService.update(partDto.getId(), memberDto);
				}
			}
		} catch (IOException e) {
			logger.error(e.toString());
		}		
	}
	
	private PartDto parseDepartment(String data) {
		String[] departments = data.split("/");
		if (departments.length < 2) {
			logger.error("Error...parseDepartment : " + data);
			return null;
		}
		
		String mainPartName = departments[0];
		String subPartName = departments[1];
		String partName = "-";
		if (departments.length == 3) {
			partName = departments[2];
		}
			
		MainPartDto mainPartDto = mainPartService.get(mainPartName);
		if (mainPartDto == null) {
			mainPartDto = MainPartDto.builder()
					.name(mainPartName)
					.latitude("35.2319")
					.longitude("128.86709")
					.mapScale("1")
					.build();
			mainPartDto = mainPartService.update(mainPartDto);
		}
		
		SubPartDto subPartDto = subPartService.get(mainPartDto.getId(), subPartName);
		if (subPartDto == null) {
			subPartDto = SubPartDto.builder()
					.name(subPartName)
					.latitude("35.2319")
					.longitude("128.86709")
					.mapScale("1")
					.mainPartDto(mainPartDto)
					.build();
			subPartDto = subPartService.update(mainPartDto.getId(), subPartDto);
		}
		
		PartDto partDto = partService.get(subPartDto.getId(), partName);
		if (partDto == null) {
			partDto = PartDto.builder()
					.name(partName)
					.subPartDto(subPartDto)
					.build();
			partDto = partService.update(subPartDto.getId(), partDto);
		}
		
		return partDto;
	}

}

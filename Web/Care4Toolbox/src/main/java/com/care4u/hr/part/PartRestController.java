package com.care4u.hr.part;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2023-11-02 박경수
 * fetch를 위해 만들었습니다
 */
@RestController
public class PartRestController {
	
	private static final Logger logger = Logger.getLogger(PartRestController.class);
	
	@Autowired
	private PartService partService;
	
   
    @GetMapping("/part/get")
    public List<PartDto> getPart(@RequestParam Long subPartId) {
        List<PartDto> partList = partService.listBySubPartId(subPartId);
        return partList;
    }
    
}
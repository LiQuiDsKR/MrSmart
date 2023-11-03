package com.care4u.hr.sub_part;


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
public class SubPartRestController {
	
	private static final Logger logger = Logger.getLogger(SubPartRestController.class);
	
	@Autowired
	private SubPartService subPartService;
	
    @GetMapping("/sub_part/get")
    public List<SubPartDto> getSubPart(@RequestParam Long mainPartId) {
        List<SubPartDto> subPartList = subPartService.listByMainPartId(mainPartId);
        return subPartList;
    }
    
}
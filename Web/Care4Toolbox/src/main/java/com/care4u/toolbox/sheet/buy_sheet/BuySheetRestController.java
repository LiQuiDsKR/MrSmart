package com.care4u.toolbox.sheet.buy_sheet;


import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care4u.constant.SheetState;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetService;
import com.care4u.toolbox.tag.TagDto;
import com.care4u.toolbox.tag.TagService;
import com.google.gson.Gson;

@RestController
public class BuySheetRestController {
	
	private static final Logger logger = Logger.getLogger(BuySheetRestController.class);
	
	@Autowired
	private BuySheetService buySheetService;
	
	
    @PostMapping(value="/buy/sheet/approve")
    public ResponseEntity<String> approveBuySheet(@Valid @RequestBody BuySheetFormDto sheetFormDto, BindingResult bindingResult){
    	if (bindingResult.hasErrors()) {
    		List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
			return ResponseEntity.badRequest().body(String.join(" / ", errors));
    	}
    	Gson gson = new Gson();
    	try {
    		buySheetService.addNew(sheetFormDto);
    	}catch(IllegalStateException e) {
    		String response = gson.toJson(sheetFormDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = gson.toJson(sheetFormDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping(value="/buy/sheet/getpage")
    public ResponseEntity<Page<BuySheetDto>> getBuySheetPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "toolboxId") long toolboxId
            ){

    	logger.info("page=" + page + ", size=" + size);
    		
        Pageable pageable = PageRequest.of(page,size);
        Page<BuySheetDto> rentalRequestSheetPage = buySheetService.getPage(toolboxId,pageable);
        
        for (BuySheetDto item : rentalRequestSheetPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(rentalRequestSheetPage);
    }
    
}
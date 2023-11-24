package com.care4u.toolbox.sheet.rental.rental_sheet;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rental")
public class RentalSheetController {
	
	private static final Logger logger = Logger.getLogger(RentalSheetController.class);
	
    @GetMapping(value = "sheet")
    public String rentalSheet(Model model){
        return "rental/sheet";
    }
    
}
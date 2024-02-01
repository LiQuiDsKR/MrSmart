package com.care4u.controller;

import org.apache.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.care4u.toolbox.sheet.rental.outstanding_rental_sheet.OutstandingRentalSheetRestController;

@Controller
public class WebSocketController {
	
	private static final Logger logger = Logger.getLogger(WebSocketController.class);
	
	private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    @GetMapping("/send-message")
    //@Scheduled(fixedRate = 5000) //그냥 5초마다 한번씩
    public void sendMessage() {
        String message = "webSocket Test Message";
        logger.info(message);
        messagingTemplate.convertAndSend("/topic/greetings", message);
    }
}

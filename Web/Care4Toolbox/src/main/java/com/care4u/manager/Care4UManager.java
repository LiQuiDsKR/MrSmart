package com.care4u.manager;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.microedition.io.StreamConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.care4u.common.GsonUtils;
import com.care4u.communication.bluetooth.BluetoothCommunicationHandler;
import com.care4u.constant.OutstandingState;
import com.care4u.constant.RequestType;
import com.care4u.constant.SheetState;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.membership.MembershipService;
import com.care4u.hr.part.PartService;
import com.care4u.hr.sub_part.SubPartService;
import com.care4u.service.LogWriterService;
import com.care4u.toolbox.tag.TagDto;
import com.care4u.toolbox.tag.TagService;
import com.care4u.toolbox.ToolboxService;
import com.care4u.toolbox.group.main_group.MainGroupService;
import com.care4u.toolbox.group.sub_group.SubGroupService;
import com.care4u.toolbox.sheet.rental.outstanding_rental_sheet.OutstandingRentalSheetDto;
import com.care4u.toolbox.sheet.rental.outstanding_rental_sheet.OutstandingRentalSheetService;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetDto;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetFormDto;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetFormWithTimestampDto;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetService;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetWithApproverAndTimestampDto;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetWithApproverIdDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetService;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetFormDto;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetFormWithTimestampDto;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetService;
import com.care4u.toolbox.stock_status.StockStatusService;
import com.care4u.toolbox.tool.ToolService;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelDto;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelService;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class Care4UManager implements InitializingBean, DisposableBean {

	private final Logger logger = LoggerFactory.getLogger(Care4UManager.class);
	
	@Autowired
	private MembershipService membershipService;
	
	@Autowired
	private ToolService toolService;
	
	@Autowired
	private RentalRequestSheetService rentalRequestSheetService;
	
	@Autowired
	private OutstandingRentalSheetService outstandingRentalSheetService;
	
	@Autowired
	private RentalSheetService rentalSheetService;
	
	@Autowired
	private ReturnSheetService returnSheetService;
	
	@Autowired
	private ToolboxToolLabelService toolboxToolLabelService;
	
	@Autowired
	private MainGroupService mainGroupService;
	
	@Autowired
	private SubGroupService subGroupService;
	
	@Autowired
	private MainPartService mainPartService;
	
	@Autowired
	private SubPartService subPartService;
	
	@Autowired
	private PartService partService;
	@Autowired
	private StockStatusService stockStatusService;
	@Autowired
	private ToolboxService toolboxService;

	@Autowired
	private TagService tagService;
	private BluetoothServer bluetoothServer;
	private BluetoothServer.Listener bluetoothServerListener = new BluetoothServer.Listener() {
		
		@Override
		public void onConnected(StreamConnection connection) {
			// TODO Auto-generated method stub
			new BluetoothCommunicationHandler("macaddress", connection, bluetoothCommunicationHandlerListener); // connection 이 정의되므로 communicationHandler를 정의함
			logger.debug("클라이언트 연결됨: " + connection.toString());
		}
	};
		
	private BluetoothCommunicationHandler.Listener bluetoothCommunicationHandlerListener = new BluetoothCommunicationHandler.Listener() {
		
		@Override
		public void onException(BluetoothCommunicationHandler handler, String message) {
			// TODO Auto-generated method stub
			logger.error("Error : " + handler.getMacaddress() + ", " + message);
		}
		
		@Override
		public void onDisconnected(BluetoothCommunicationHandler handler) {
			// TODO Auto-generated method stub
			workersPool.remove(handler);
			logger.info("workersPool size : " + workersPool.size() + ", Bye : " + handler.getMacaddress());
		}
		
		@Override
		public void onDataSent(BluetoothCommunicationHandler handler, String data) {
			// TODO Auto-generated method stub
			logger.info("Sent: " + data);
		}
		
		@Override
		public void onDataArrived(BluetoothCommunicationHandler handler, String data) {			
			process(handler, data);
		}
		
		@Override
		public void onConnected(BluetoothCommunicationHandler handler) {			
			// TODO Auto-generated method stub
			logger.info(handler.toString());
			workersPool.add(handler);
			
			/*
			sendMembership(handler);
			handler.sendData("RESPONSE_FINISH");
			sendTool(handler);
			handler.sendData("RESPONSE_FINISH");
			*/
		}
		/* 요청에 대한 응답이 더 필요하면 이것처럼 만들기
		public void onRequestSheetArrived(BluetoothCommunicationHandler handler, RentalSheet rentalSheet) {
			List<ToolDto> toolDtoList = toolService.list();
			Message command = new Message(GlobalConstants.REQUEST_TOOL_LIST, toolDtoList);
			handler.send(command);
		}*/
	};
	
	private final List<BluetoothCommunicationHandler> workersPool = new CopyOnWriteArrayList<BluetoothCommunicationHandler>();
	
	private Timer timer;
	private final TimerTask timerTask = new TimerTask(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Iterator<BluetoothCommunicationHandler> iterator = workersPool.iterator();
			BluetoothCommunicationHandler worker = null;
			while(iterator.hasNext()) {
				worker = iterator.next();
				if (!worker.isValid()) {
					worker.disconnect();
					workersPool.remove(worker);
				}
			}
		}
	};
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

		//MemberParsing memberParsing = new MemberParsing(mainPartService, subPartService, partService, membershipService);
		//memberParsing.readCsvFile("C:/Care4U/Temp/member.csv");
		
		//ToolParsing toolParsing = new ToolParsing(mainGroupService, subGroupService, toolService, stockStatusService, toolboxService);
		//toolParsing.readCsvFile("C:/Care4u/Temp/tool.csv");
		//toolParsing.checkCsvFile("C:/Care4u/Temp/tool.csv");
		
		//stockStatusService.addMock();
		//tagService.addMock();
		
		bluetoothServer = new BluetoothServer(bluetoothServerListener);
		
		//stockStatusService.addMock();
		//tagService.addMock();
		//membershipService.updatePasswords();
		//membershipService.downdatePasswords();
		
		timer = new Timer();
		timer.schedule(timerTask, 10 * 1000, 1000);
		
		logger.info("Care4UManager  afterPropertiesSet... ");
	}
	
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
		logger.info("Care4UManager destroy... ");
		
		if (timer != null) {
		    timer.cancel();
		    timer = null;
		}
		
		bluetoothServer.destroy();
		
		Iterator<BluetoothCommunicationHandler> iterator = workersPool.iterator();
		while(iterator.hasNext()) {
			iterator.next().disconnect();
		}
		workersPool.clear();
	}
	
	private void process(BluetoothCommunicationHandler handler, String data) {
		logger.info("Arrived: " + data); // 지금 전달받은 내용 data 로 출력 (로그)
		String[] datas = data.split(",",2);
		RequestType type = RequestType.valueOf(datas[0]);
		String paramJson = null;
		if (datas.length>1) {				
			paramJson = datas[1];
		}
		String keyword = type.name() + ",";
		switch(type) {
		case MEMBERSHIP_ALL_COUNT:
			handler.sendData(keyword+GsonUtils.toJson(membershipService.getMembershipCount()));
			break;
		case MEMBERSHIP_ALL:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				int page = jsonObj.getInt("page");
				int pageSize = jsonObj.getInt("size");
				
				Pageable pageable = PageRequest.of(page,pageSize);
				handler.sendData(keyword + GsonUtils.toJson(membershipService.getMembershipPage(pageable)));
			}
			break;
		case TOOL_ALL_COUNT:
			handler.sendData(keyword+GsonUtils.toJson(toolService.getToolCount()));
			break;
		case TOOL_ALL:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				int page = jsonObj.getInt("page");
				int pageSize = jsonObj.getInt("size");
				
				Pageable pageable = PageRequest.of(page,pageSize);
				handler.sendData(keyword + GsonUtils.toJson(toolService.getToolPage(pageable)));
			}
			break;
		case RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				int page = jsonObj.getInt("page");
				int pageSize = jsonObj.getInt("size");
				long membershipId = jsonObj.getLong("membershipId");

		        Pageable pageable = PageRequest.of(page,pageSize);
		        Page<RentalRequestSheetDto> sheetPage = rentalRequestSheetService.getPageByMembership(SheetState.READY,membershipId,pageable);
				handler.sendData(keyword + GsonUtils.toJson(sheetPage));
			}
			break;
		case RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP_COUNT:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				long membershipId = jsonObj.getLong("membershipId");
		        Long count = rentalRequestSheetService.getCountByMembership(SheetState.READY,membershipId);
				handler.sendData(keyword + GsonUtils.toJson(count));
			}
			break;
		case RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				int page = jsonObj.getInt("page");
				int pageSize = jsonObj.getInt("size");
				long toolboxId = jsonObj.getLong("toolboxId");

		        Pageable pageable = PageRequest.of(page,pageSize);
		        Page<RentalRequestSheetDto> sheetPage = rentalRequestSheetService.getPageByToolbox(SheetState.REQUEST,toolboxId,pageable);
				handler.sendData(keyword + GsonUtils.toJson(sheetPage));
			}
			break;
//		case RENTAL_REQUEST_SHEET_LIST_BY_TOOLBOX:
//			if (!(paramJson.isEmpty() || paramJson==null)) {
//				JSONObject jsonObj = new JSONObject(paramJson);
//				long toolboxId = jsonObj.getLong("toolboxId");
//
//		        List<RentalRequestSheetDto> sheetList = rentalRequestSheetService.getList(SheetState.REQUEST,toolboxId);
//				handler.sendData(keyword + GsonUtils.toJson(sheetList));
//			}
//			break;
		case RENTAL_REQUEST_SHEET_FORM:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				RentalRequestSheetFormDto formDto;
		    	try {
					formDto = GsonUtils.fromJson(paramJson, RentalRequestSheetFormDto.class);
		    		rentalRequestSheetService.addNew(formDto);
		    		handler.sendData(keyword + "good");
		    	}catch(Exception e) {
		    		handler.sendData(keyword + "bad");
		    	}
			}
			break;
		case RENTAL_REQUEST_SHEET_FORM_STANDBY:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				RentalRequestSheetFormWithTimestampDto mainDto;
		    	try {
					mainDto = GsonUtils.fromJson(paramJson, RentalRequestSheetFormWithTimestampDto.class);
					RentalRequestSheetFormDto formDto = mainDto.getSheet();
					String timeString = mainDto.getTimestamp();
					
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			        LocalDateTime eventTimestamp = LocalDateTime.parse(timeString, formatter);
			        
		    		rentalRequestSheetService.addNew(formDto,eventTimestamp);
		    		handler.sendData(keyword + "good");
		    	}catch(Exception e) {
		    		handler.sendData(keyword + "bad");
		    	}
			}
			break;
		case RENTAL_REQUEST_SHEET_APPROVE_STANDBY:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				RentalRequestSheetWithApproverAndTimestampDto mainDto;
		    	try {
					mainDto = GsonUtils.fromJson(paramJson, RentalRequestSheetWithApproverAndTimestampDto.class);
					RentalRequestSheetDto sheetDto = mainDto.getSheet().getRentalRequestSheetDto();
					long approverId = mainDto.getSheet().getApproverId();
					String timeString = mainDto.getTimestamp();
					
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			        LocalDateTime eventTimestamp = LocalDateTime.parse(timeString, formatter);
					
		            RentalSheetDto result = rentalSheetService.updateAndAddNewInTransaction(sheetDto, approverId, eventTimestamp);
		            
		    		handler.sendData(keyword + "good");
		    	}catch(IllegalStateException e) {
		    		handler.sendData(keyword + "bad");
		    	}catch(Exception e) {
		    		handler.sendData(keyword + "bad");
		    	}
			}
			break;
		case RENTAL_REQUEST_SHEET_APPROVE:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				RentalRequestSheetWithApproverIdDto mainDto;
		    	try {
					mainDto = GsonUtils.fromJson(paramJson, RentalRequestSheetWithApproverIdDto.class);
					RentalRequestSheetDto sheetDto = mainDto.getRentalRequestSheetDto();
					long approverId = mainDto.getApproverId();
					
		            RentalSheetDto result = rentalSheetService.updateAndAddNewInTransaction(sheetDto, approverId);
		            
		    		handler.sendData(keyword + "good");
		    	}catch(IllegalStateException e) {
		    		handler.sendData(keyword + "bad");
		    	}catch(Exception e) {
		    		handler.sendData(keyword + "bad");
		    	}
			}
			break;
		case RENTAL_REQUEST_SHEET_CANCEL:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				long sheetId = jsonObj.getLong("rentalRequestSheetId");
		    	try {
		    		rentalRequestSheetService.cancel(sheetId);
		    		handler.sendData(keyword + "good");
		    	}catch(IllegalStateException e) {
		    		handler.sendData(keyword + "bad");
		    	}catch(Exception e) {
		    		handler.sendData(keyword + "bad");
		    	}
			}
			break;
		case RETURN_SHEET_PAGE_BY_MEMBERSHIP:
			break;
		case OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				int page = jsonObj.getInt("page");
				int pageSize = jsonObj.getInt("size");
				long membershipId = jsonObj.getLong("membershipId");
		        Pageable pageable = PageRequest.of(page,pageSize);
		        Page<OutstandingRentalSheetDto> sheetPage = outstandingRentalSheetService.getPageByMembershipId(OutstandingState.READY,membershipId, pageable);
		        
				handler.sendData(keyword + GsonUtils.toJson(sheetPage));
			}
			break;
		case OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP_COUNT:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				long membershipId = jsonObj.getLong("membershipId");
		       	Long count = outstandingRentalSheetService.getCountByMembershipId(OutstandingState.READY,membershipId);
		        
				handler.sendData(keyword + GsonUtils.toJson(count));
			}
			break;
//		case OUTSTANDING_RENTAL_SHEET_LIST_BY_MEMBERSHIP:
//			if (!(paramJson.isEmpty() || paramJson==null)) {
//				JSONObject jsonObj = new JSONObject(paramJson);
//				long membershipId = jsonObj.getLong("membershipId");
//	    		//String startDate = jsonObj.getString("startDate");
//				//String endDate = jsonObj.getString("endDate");
//
//		        //LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
//		        //LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
//
//		        List<OutstandingRentalSheetDto> sheetList = outstandingRentalSheetService.getListByMembershipId(membershipId);
//		        
//				handler.sendData(keyword + GsonUtils.toJson(sheetList));
//			}
//			break;	
		case OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				int page = jsonObj.getInt("page");
				int pageSize = jsonObj.getInt("size");
				long toolboxId = jsonObj.getLong("toolboxId");
	    		String startDate = jsonObj.getString("startDate");
				String endDate = jsonObj.getString("endDate");

		        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
		        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

		        Pageable pageable = PageRequest.of(page,pageSize);
		        Page<OutstandingRentalSheetDto> sheetPage = outstandingRentalSheetService.getPageByToolboxId(toolboxId, startLocalDate, endLocalDate, pageable);
				handler.sendData(keyword + GsonUtils.toJson(sheetPage));
			}
			break;
//		case OUTSTANDING_RENTAL_SHEET_LIST_BY_TOOLBOX:
//			if (!(paramJson.isEmpty() || paramJson==null)) {
//				JSONObject jsonObj = new JSONObject(paramJson);
//				long toolboxId = jsonObj.getLong("toolboxId");
//	    		//String startDate = jsonObj.getString("startDate");
//				//String endDate = jsonObj.getString("endDate");
//
//		        //LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
//		        //LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
//
//		        List<OutstandingRentalSheetDto> sheetList = outstandingRentalSheetService.getListByToolboxId(toolboxId);
//				handler.sendData(keyword + GsonUtils.toJson(sheetList));
//			}
//			break;
		case OUTSTANDING_RENTAL_SHEET_BY_TAG:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				String tag = jsonObj.getString("tag");
				
				OutstandingRentalSheetDto sheet = outstandingRentalSheetService.get(tag);
				handler.sendData(keyword + GsonUtils.toJson(sheet));
			}
			break;
		case RETURN_SHEET_REQUEST:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				long outstandingRentalSheetId = jsonObj.getLong("outstandingRentalSheetId");
		    	try {
		    		outstandingRentalSheetService.requestOutstandingState(outstandingRentalSheetId);
		    		handler.sendData(keyword + "good");
		    	}catch(IllegalStateException e) {
		    		handler.sendData(keyword + "bad");
		    	}catch(Exception e) {
		    		handler.sendData(keyword + "bad");
		    	}
			}
			break;
		case RETURN_SHEET_FORM:
			if (!(paramJson.isEmpty() || paramJson==null)) {
		    	ReturnSheetFormDto formDto;
		    	try {
					formDto = GsonUtils.fromJson(paramJson, ReturnSheetFormDto.class);
		    		returnSheetService.addNew(formDto);
		    		handler.sendData(keyword + "good");
		    	}catch(Exception e) {
		    		handler.sendData(keyword + "bad");
		    	}
			}
			break;
		case RETURN_SHEET_FORM_STANDBY:
			if (!(paramJson.isEmpty() || paramJson==null)) {
		    	ReturnSheetFormWithTimestampDto mainDto;
		    	try {
					mainDto = GsonUtils.fromJson(paramJson, ReturnSheetFormWithTimestampDto.class);
					ReturnSheetFormDto formDto = mainDto.getSheet();
					String timeString = mainDto.getTimestamp();
					
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			        LocalDateTime eventTimestamp = LocalDateTime.parse(timeString, formatter);
			        
					
		    		returnSheetService.addNew(formDto,eventTimestamp);
		    		handler.sendData(keyword + "good");
		    	}catch(Exception e) {
		    		handler.sendData(keyword + "bad");
		    	}
			}
			break;
		case TOOLBOX_TOOL_LABEL_FORM:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				long toolId = jsonObj.getLong("toolId");
				long toolboxId = jsonObj.getLong("toolboxId");
				String qrcode = jsonObj.getString("qrcode");
		    	try {
					toolboxToolLabelService.register(toolId, toolboxId, qrcode);
		    		handler.sendData(keyword + "good");
		    	}catch(IllegalArgumentException e) {
		    		handler.sendData(e.getMessage());
		    	}catch(Exception e) {
		    		handler.sendData(keyword + "bad");
		    	}
			}
			break;
		case TAG_FORM:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				long toolId = jsonObj.getLong("toolId");
				long toolboxId = jsonObj.getLong("toolboxId");
				String tagGroup = jsonObj.getString("tagGroup");
				JSONArray tagListArray = jsonObj.getJSONArray("tagList");
		        List<String> tagList = new ArrayList<String>();
		        for (int i = 0; i < tagListArray.length(); i++) {
		            tagList.add(tagListArray.getString(i));
		        }

		    	try {
					tagService.register(toolId, toolboxId, tagList, tagGroup);
		    		handler.sendData(keyword + "good");
		    	}catch(IllegalArgumentException e) {
		    		handler.sendData(keyword + e.getMessage());
		    	}catch(Exception e) {
		    		handler.sendData(keyword + "bad");
		    	}
			}
			break;
		case TOOLBOX_TOOL_LABEL:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				long toolId = jsonObj.getLong("toolId");
				long toolboxId = jsonObj.getLong("toolboxId");
				
				if (toolboxToolLabelService.get(toolId, toolboxId) == null) {
					handler.sendData(keyword + "null");
				}					
				else {	
					String result = toolboxToolLabelService.get(toolId, toolboxId).getQrcode();
					handler.sendData(keyword + result);
				}
			}
			break;
		case TAG_LIST:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				String tagString = jsonObj.getString("tag");				
				if (tagService.getSiblings(tagString) != null) {
					List<String> strings= tagService.getSiblings(tagString)
							.stream()
							.map(e->e.getMacaddress())
							.collect(Collectors.toList());
					handler.sendData(keyword + GsonUtils.toJson(strings));
				} else {
					handler.sendData(keyword + "null");
				}
			}
			break;
		case TAG_ALL:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				long toolboxId = jsonObj.getLong("toolboxId");
				int page = jsonObj.getInt("page");
				int pageSize = jsonObj.getInt("size");
				Pageable pageable = PageRequest.of(page,pageSize);
		        Page<TagDto> tagPage = tagService.getPage(toolboxId,pageable);
				handler.sendData(keyword + GsonUtils.toJson(tagPage));
			}
			break;
		case TAG_ALL_COUNT:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				long toolboxId = jsonObj.getLong("toolboxId");
				handler.sendData(keyword + GsonUtils.toJson(tagService.getCount(toolboxId)));
			}
			break;
		case TOOLBOX_TOOL_LABEL_ALL:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				long toolboxId = jsonObj.getLong("toolboxId");
				int page = jsonObj.getInt("page");
				int pageSize = jsonObj.getInt("size");
				Pageable pageable = PageRequest.of(page,pageSize);
				Page<ToolboxToolLabelDto> labelPage = toolboxToolLabelService.getPage(toolboxId,pageable);
				handler.sendData(keyword + GsonUtils.toJson(labelPage));
			}
			break;
		case TOOLBOX_TOOL_LABEL_ALL_COUNT:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				long toolboxId = jsonObj.getLong("toolboxId");
				handler.sendData(keyword + GsonUtils.toJson(toolboxToolLabelService.getCount(toolboxId)));
			}
			break;
		case TAG_GROUP:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				String tagString = jsonObj.getString("tag");
				handler.sendData(keyword + GsonUtils.toJson(tagService.getSiblings(tagString)));
			}
		case TAG:
			if (!(paramJson.isEmpty() || paramJson==null)) {
				JSONObject jsonObj = new JSONObject(paramJson);
				String tagString = jsonObj.getString("tag");
				handler.sendData(keyword + GsonUtils.toJson(tagService.get(tagString)));
			}
		}
	}
	
	
}


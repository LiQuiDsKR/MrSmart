package com.care4u.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.care4u.common.GlobalConstants;
import com.care4u.common.GsonUtils;
import com.care4u.communication.bluetooth.BluetoothCommunicationHandler;
import com.care4u.constant.EmploymentState;
import com.care4u.constant.RequestType;
import com.care4u.constant.Role;
import com.care4u.constant.SheetState;
import com.care4u.domain.Message;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.hr.membership.MembershipService;
import com.care4u.hr.part.PartService;
import com.care4u.hr.sub_part.SubPartService;
import com.care4u.service.LogWriterService;
import com.care4u.toolbox.group.main_group.MainGroupDto;
import com.care4u.toolbox.group.main_group.MainGroupService;
import com.care4u.toolbox.group.sub_group.SubGroupService;
import com.care4u.toolbox.stock_status.StockStatusService;
import com.care4u.toolbox.tag.Tag;
import com.care4u.toolbox.tag.TagDto;
import com.care4u.toolbox.tag.TagService;
import com.care4u.toolbox.sheet.rental.outstanding_rental_sheet.OutstandingRentalSheetDto;
import com.care4u.toolbox.sheet.rental.outstanding_rental_sheet.OutstandingRentalSheetService;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetDto;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetFormDto;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetService;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetWithApproverIdDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetService;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetFormDto;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetService;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.tool.ToolService;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelDto;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class Care4UManager implements InitializingBean, DisposableBean {

	private final Logger logger = LoggerFactory.getLogger(Care4UManager.class);
	
	private final LogWriterService mLogWriterService;
	
	@Autowired
	private MainPartService mainPartService;
	
	@Autowired
	private SubPartService subPartService;
	
	@Autowired
	private PartService partService;
	
	@Autowired
	private MembershipService membershipService;
	
	@Autowired
	private MainGroupService mainGroupService;
	
	@Autowired
	private SubGroupService subGroupService;
	
	@Autowired
	private ToolService toolService;
	
	@Autowired
	private StockStatusService stockStatusService;
	
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
	private TagService tagService;
	private BluetoothServer bluetoothServer;
	private BluetoothServer.Listener bluetoothServerListener = new BluetoothServer.Listener() {
		
		@Override
		public void onConnected(StreamConnection connection) {
			// TODO Auto-generated method stub
			BluetoothCommunicationHandler communicationHandler = new BluetoothCommunicationHandler("macaddress", connection, bluetoothCommunicationHandlerListener); // connection 이 정의되므로 communicationHandler를 정의함
			logger.debug("클라이언트 연결됨: " + connection.toString());
		}
	};
		
	private List<BluetoothCommunicationHandler> workersPool = new CopyOnWriteArrayList<BluetoothCommunicationHandler>();
	private BluetoothCommunicationHandler.Listener bluetoothCommunicationHandlerListener = new BluetoothCommunicationHandler.Listener() {
		
		@Override
		public void onException(BluetoothCommunicationHandler handler, String message) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDisconnected(BluetoothCommunicationHandler handler) {
			// TODO Auto-generated method stub
			workersPool.remove(handler);
			logger.info("workersPool size : " + workersPool.size() + ", Bye : " + handler.getMacaddress());
		}
		
		@Override
		public void onDataSent(BluetoothCommunicationHandler handler, int size, String data) {
			// TODO Auto-generated method stub
			logger.info("Sent: " + data);
		}
		
		@Override
		public void onDataArrived(BluetoothCommunicationHandler handler, int size, String data) {
			
			logger.info("Arrived: " + data); // 지금 전달받은 내용 data 로 출력 (로그)
			String[] datas = data.split(",",2);
			RequestType type = RequestType.valueOf(datas[0]);
			String paramJson = null;
			if (datas.length>1) {				
				paramJson = datas[1];
			}
			switch(type) {
			case MEMBERSHIP_ALL:
				handler.sendData(GsonUtils.toJson(membershipService.list()));
				break;
			case TOOL_ALL:
				handler.sendData(GsonUtils.toJson(toolService.list()));
				break;
			case RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX:
				if (!(paramJson.isEmpty() || paramJson==null)) {
					JSONObject jsonObj = new JSONObject(paramJson);
					int page = jsonObj.getInt("page");
					int pageSize = jsonObj.getInt("size");
					long toolboxId = jsonObj.getLong("toolboxId");

			        Pageable pageable = PageRequest.of(page,pageSize);
			        Page<RentalRequestSheetDto> sheetPage = rentalRequestSheetService.getPage(SheetState.REQUEST,toolboxId,pageable);
					handler.sendData(GsonUtils.toJson(sheetPage));
				}
				break;
			case RENTAL_REQUEST_SHEET_LIST_BY_TOOLBOX:
				if (!(paramJson.isEmpty() || paramJson==null)) {
					JSONObject jsonObj = new JSONObject(paramJson);
					long toolboxId = jsonObj.getLong("toolboxId");

			        List<RentalRequestSheetDto> sheetList = rentalRequestSheetService.getList(SheetState.REQUEST,toolboxId);
					handler.sendData(GsonUtils.toJson(sheetList));
				}
				break;
			case RENTAL_REQUEST_SHEET_FORM:
				if (!(paramJson.isEmpty() || paramJson==null)) {
					RentalRequestSheetFormDto formDto;
			    	try {
						formDto = GsonUtils.fromJson(paramJson, RentalRequestSheetFormDto.class);
			    		rentalRequestSheetService.addNew(formDto);
			    		handler.sendData("good");
			    	}catch(Exception e) {
			    		handler.sendData("bad");
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
			            
			    		handler.sendData("good");
			    	}catch(IllegalStateException e) {
			    		handler.sendData("bad");
			    	}catch(Exception e) {
			    		handler.sendData("bad");
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
		    		String startDate = jsonObj.getString("startDate");
					String endDate = jsonObj.getString("endDate");

			        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
			        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

			        Pageable pageable = PageRequest.of(page,pageSize);
			        Page<OutstandingRentalSheetDto> sheetPage = outstandingRentalSheetService.getPageByMembershipId(membershipId, startLocalDate, endLocalDate, pageable);
			        
					handler.sendData(GsonUtils.toJson(sheetPage));
				}
				break;
			case OUTSTANDING_RENTAL_SHEET_LIST_BY_MEMBERSHIP:
				if (!(paramJson.isEmpty() || paramJson==null)) {
					JSONObject jsonObj = new JSONObject(paramJson);
					long membershipId = jsonObj.getLong("membershipId");
		    		//String startDate = jsonObj.getString("startDate");
					//String endDate = jsonObj.getString("endDate");

			        //LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
			        //LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

			        List<OutstandingRentalSheetDto> sheetList = outstandingRentalSheetService.getListByMembershipId(membershipId);
			        
					handler.sendData(GsonUtils.toJson(sheetList));
				}
				break;	
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
					handler.sendData(GsonUtils.toJson(sheetPage));
				}
				break;
			case OUTSTANDING_RENTAL_SHEET_LIST_BY_TOOLBOX:
				if (!(paramJson.isEmpty() || paramJson==null)) {
					JSONObject jsonObj = new JSONObject(paramJson);
					long toolboxId = jsonObj.getLong("toolboxId");
		    		//String startDate = jsonObj.getString("startDate");
					//String endDate = jsonObj.getString("endDate");

			        //LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
			        //LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

			        List<OutstandingRentalSheetDto> sheetList = outstandingRentalSheetService.getListByToolboxId(toolboxId);
					handler.sendData(GsonUtils.toJson(sheetList));
				}
				break;
			case RETURN_SHEET_REQUEST:
				if (!(paramJson.isEmpty() || paramJson==null)) {
					JSONObject jsonObj = new JSONObject(paramJson);
					long outstandingRentalSheetId = jsonObj.getLong("outstandingRentalSheetId");
			    	try {
			    		outstandingRentalSheetService.requestOutstandingState(outstandingRentalSheetId);
			    		handler.sendData("good");
			    	}catch(IllegalStateException e) {
			    		handler.sendData("bad");
			    	}catch(Exception e) {
			    		handler.sendData("bad");
			    	}
				}
				break;
			case RETURN_SHEET_FORM:
				if (!(paramJson.isEmpty() || paramJson==null)) {
			    	ReturnSheetFormDto formDto;
			    	try {
						formDto = GsonUtils.fromJson(paramJson, ReturnSheetFormDto.class);
			    		returnSheetService.addNew(formDto);
			    		handler.sendData("good");
			    	}catch(Exception e) {
			    		handler.sendData("bad");
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
			    		handler.sendData("good");
			    	}catch(IllegalArgumentException e) {
			    		handler.sendData(e.getMessage());
			    	}catch(Exception e) {
			    		handler.sendData("bad");
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
			    		handler.sendData("good");
			    	}catch(IllegalArgumentException e) {
			    		handler.sendData(e.getMessage());
			    	}catch(Exception e) {
			    		handler.sendData("bad");
			    	}
				}
				break;
			case TOOLBOX_TOOL_LABEL:
				if (!(paramJson.isEmpty() || paramJson==null)) {
					JSONObject jsonObj = new JSONObject(paramJson);
					long toolId = jsonObj.getLong("toolId");
					long toolboxId = jsonObj.getLong("toolboxId");
					
					String result = toolboxToolLabelService.get(toolId, toolboxId).getQrcode();
					if (result==null) {
						handler.sendData("null");
					}else {				
						handler.sendData(result);
					}
				}
				break;
			case TAG_LIST:
				if (!(paramJson.isEmpty() || paramJson==null)) {
					JSONObject jsonObj = new JSONObject(paramJson);
					String tagString = jsonObj.getString("tag");
					List<String> strings= tagService.getSiblings(tagString)
							.stream()
							.map(e->e.getMacaddress())
							.collect(Collectors.toList());
					
					handler.sendData(GsonUtils.toJson(strings));
				}
				break;
			case TAG_ALL:
				if (!(paramJson.isEmpty() || paramJson==null)) {
					JSONObject jsonObj = new JSONObject(paramJson);
					long toolboxId = jsonObj.getLong("toolboxId");
			        List<TagDto> tagList = tagService.listByToolboxId(toolboxId);
					handler.sendData(GsonUtils.toJson(tagList));
				}
				break;
			case TOOLBOX_TOOL_LABEL_ALL:
				if (!(paramJson.isEmpty() || paramJson==null)) {
					JSONObject jsonObj = new JSONObject(paramJson);
					long toolboxId = jsonObj.getLong("toolboxId");
			        List<ToolboxToolLabelDto> tagList = toolboxToolLabelService.listByToolboxId(toolboxId);
					handler.sendData(GsonUtils.toJson(tagList));
				}
				break;
			case TAG_GROUP:
				if (!(paramJson.isEmpty() || paramJson==null)) {
					JSONObject jsonObj = new JSONObject(paramJson);
					String tagString = jsonObj.getString("tag");
					handler.sendData(GsonUtils.toJson(tagService.getTagGroup(tagString)));
				}
			}
		}
		
		@Override
		public void onConnected(BluetoothCommunicationHandler handler) {
			logger.info(handler.toString());
			
			// TODO Auto-generated method stub
			/*
			workersPool.add(handler);
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
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

		//MemberParsing memberParsing = new MemberParsing(mainPartService, subPartService, partService, membershipService);
		//memberParsing.readCsvFile("C:/Temp/Temp/member.csv");
		
		//ToolParsing toolParsing = new ToolParsing(mainGroupService, subGroupService, toolService);
		//toolParsing.readCsvFile("C:/Temp/Temp/tool.csv");D
		
		//stockStatusService.addMock();
		//tagService.addMock();
		
		bluetoothServer = new BluetoothServer(bluetoothServerListener);
		
		//stockStatusService.addMock();
		//tagService.addMock();
		//membershipService.updatePasswords();
		//membershipService.downdatePasswords();
		
		logger.info("Care4UManager  afterPropertiesSet... ");
	}
	
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
		logger.info("Care4UManager destroy... ");
		
		bluetoothServer.destroy();
		
		Iterator<BluetoothCommunicationHandler> iterator = workersPool.iterator();
		while(iterator.hasNext()) {
			iterator.next().destroy();
		}
	}
	
	
	private void writeDeviceMonitorDto(MainGroupDto dto) {
		
		File directoryFile = new File(GlobalConstants.DEVICE_MONITOR_HOME_DIRECTORY);
		if (!directoryFile.exists()) {
			directoryFile.mkdir();
		}
		
		String filename = dto.getName().trim();
		String filepath = directoryFile.getAbsolutePath() + GlobalConstants.FILE_SEPERATOR + filename + "_" + Calendar.getInstance().getTimeInMillis() +".log";
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(filepath, true);
			bw = new BufferedWriter(fw);
			bw.write(GsonUtils.toJson(dto));
			bw.flush();
			logger.debug("save : " + filepath);
			logger.debug("save DeviceMonitorDto : " + dto.toString());
		} catch (Exception e) {
			logger.error("Write Error SensorKit File : " + filepath, e);
			mLogWriterService.write(GlobalConstants.SERVER_LOG, "Error!!! write SensorKit File : " + filepath + ", " + e.toString());
		} finally{
			if (bw != null) try{bw.close();} catch (IOException e) {}
			if (fw != null) try{fw.close();} catch (IOException e) {}
		}
	}
	
	public String getMembership(BluetoothCommunicationHandler handler) {
	    List<MembershipDto> memberDtoList = membershipService.list();
	    Message message = new Message(GlobalConstants.REQUEST_MEMBER_LIST, memberDtoList);	  
	    logger.info(GsonUtils.toJson(message));
	    //handler.sendData(GsonUtils.toJson(message));
	    return GsonUtils.toJson(message);
	}
	public String getTool(BluetoothCommunicationHandler handler) {
		List<ToolDto> toolDtoList = toolService.list();
	    Message message = new Message(GlobalConstants.REQUEST_TOOL_LIST, toolDtoList);	 
	    logger.info(GsonUtils.toJson(message));    
	    //handler.sendData(GsonUtils.toJson(message));
	    return GsonUtils.toJson(message);
	}
	public void sendSize(BluetoothCommunicationHandler handler, String sendingMessage) { // 쓸모없어서 삭제예정
		int size = sendingMessage.getBytes().length;
		handler.sendData(Integer.toString(size));
		logger.info(Integer.toString(size));
	}
	public void sendMessage(BluetoothCommunicationHandler handler, String sendingMessage) { // 쓸모없어서 삭제예정
		handler.sendData(sendingMessage);
	}
}


package com.care4u.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.io.StreamConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.care4u.common.GlobalConstants;
import com.care4u.common.GsonUtils;
import com.care4u.communication.bluetooth.BluetoothCommunicationHandler;
import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
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
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.tool.ToolService;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class Care4UManager implements InitializingBean, DisposableBean {

	private final Logger logger = LoggerFactory.getLogger(Care4UManager.class);
	private Gson gson = new Gson();
	
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
			// TODO Auto-generated method stub
			logger.info("Arrived: " + data); // 지금 전달받은 내용 data 로 출력 (로그)
		}
		
		@Override
		public void onConnected(BluetoothCommunicationHandler handler) {
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
	    logger.info(gson.toJson(message));
	    //handler.sendData(gson.toJson(message));
	    return gson.toJson(message);
	}
	public String getTool(BluetoothCommunicationHandler handler) {
		List<ToolDto> toolDtoList = toolService.list();
	    Message message = new Message(GlobalConstants.REQUEST_TOOL_LIST, toolDtoList);	 
	    logger.info(gson.toJson(message));    
	    //handler.sendData(gson.toJson(message));
	    return gson.toJson(message);
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


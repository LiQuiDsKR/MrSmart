package com.care4u.manager;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.care4u.communication.bluetooth.BluetoothCommunicationHandler;
import com.care4u.communication.bluetooth.BluetoothConnectHandler;
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
import com.care4u.toolbox.group.main_group.MainGroupService;
import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.group.sub_group.SubGroupService;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.tool.ToolService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BluetoothServer extends Thread implements InitializingBean, DisposableBean {
	
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
	
	StreamConnection connection;
	private static final Logger logger = LoggerFactory.getLogger(BluetoothServer.class);
	private List<BluetoothCommunicationHandler> workersPool = new CopyOnWriteArrayList<BluetoothCommunicationHandler>();
	BluetoothCommunicationHandler communicationHandler;
	BluetoothConnectHandler connectHandler;
	
	private BluetoothConnectHandler.Listener bluetoothConnectHandlerListener = new BluetoothConnectHandler.Listener() {
		
		@Override
		public void onException(String description) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDisconnected() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDataArrived(byte[] datas) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onConnected() {
			// TODO Auto-generated method stub
			
		}
	};
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
			
		}
		
		@Override
		public void onDataArrived(BluetoothCommunicationHandler handler, int size, String data) {
			// TODO Auto-generated method stub
			logger.info("Arrived: " + data);
		}
		
		@Override
		public void onConnected(BluetoothCommunicationHandler handler) {
			// TODO Auto-generated method stub
			workersPool.add(handler);
			sendMembership();	
		}
	};
	
	private boolean stopNow;

	public BluetoothServer() {
		this.start();
	}

	public void sendMembership() {
	    List<MembershipDto> memberDtoList = membershipService.list();
	    long id = 0;
	    String code = null;
	    String password = null;
	    String name = null;
	    String part = null;
	    //String subPart = null;
	    //String mainPart = null;
	    Role role = null;
	    EmploymentState employmentState = null;
	    
	    String data;
	    for (MembershipDto member : memberDtoList) {
	    	id = member.getId();
	    	code = member.getCode();
	    	password = member.getPassword();
	    	name = member.getName();
	    	part = member.getPartDto().getName();
	    	//subPart = member.getPartDto().getSubPartDto().getName();
	    	//mainPart = member.getPartDto().getSubPartDto().getMainPartDto().getName();
	    	role = member.getRole();
	    	employmentState = member.getEmploymentStatus();
	    	data = id + "," + code + "," + password + "," + name + "," + part + "," + role + "," + employmentState + "\n";
	    	//data = id + "," + code + "," + password + "," + name + "," + part + "," + subPart + "," + mainPart + "," + role + "," + employmentState + "\n";
		    connectHandler.sendData(data.getBytes());
	    }
	    connectHandler.sendData("EndMembership".getBytes());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// Bluetooth 디바이스를 검색합니다.
			LocalDevice localDevice = LocalDevice.getLocalDevice();
			logger.info("Bluetooth Adapter: " + localDevice.getBluetoothAddress());
			
			// 서버 소켓을 만듭니다.
			String url = "btspp://localhost:" + new UUID("0000110100001000800000805F9B34FB", false).toString() + ";name=BluetoothServer";
			StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector.open(url);
			logger.info("Bluetooth Server 시작...");
			int count = 1;
			stopNow = false;
			while (!stopNow) {
				// 클라이언트 연결을 대기합니다.
				connection = notifier.acceptAndOpen();
				communicationHandler = new BluetoothCommunicationHandler("macaddress", connection, bluetoothCommunicationHandlerListener); // connection 이 정의되므로 communicationHandler를 정의함
				connectHandler = new BluetoothConnectHandler(connection, bluetoothConnectHandlerListener);		// connection 이 정의되므로 connectHandler를 정의함
				logger.debug("클라이언트 연결됨: " + connection.toString());
				
				// 데이터를 주고받을 스레드를 시작합니다.
				new BluetoothCommunicationHandler("macaddress" + count, connection, bluetoothCommunicationHandlerListener);
				count++;
			}
		} catch (Exception e) {
		    logger.error("Bluetooth Server Died...", e);
		}
		
	}

	public void onDestroy() {
		if (!stopNow) {
			stopNow = true;
			this.interrupt();
		}
		
		Iterator<BluetoothCommunicationHandler> iterator = workersPool.iterator();
		while(iterator.hasNext()) {
			iterator.next().destroy();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
	}
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub	
	}

}

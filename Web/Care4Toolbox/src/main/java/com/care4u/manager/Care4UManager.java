package com.care4u.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.care4u.common.GlobalConstants;
import com.care4u.common.GsonUtils;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.membership.MembershipService;
import com.care4u.hr.part.PartService;
import com.care4u.hr.sub_part.SubPartService;
import com.care4u.service.LogWriterService;
import com.care4u.toolbox.group.main_group.MainGroupDto;
import com.care4u.toolbox.group.main_group.MainGroupService;
import com.care4u.toolbox.group.sub_group.SubGroupService;
import com.care4u.toolbox.stock_status.StockStatusService;
import com.care4u.toolbox.tag.TagService;
import com.care4u.toolbox.tool.ToolService;

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
	private TagService tagService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

		//MemberParsing memberParsing = new MemberParsing(mainPartService, subPartService, partService, membershipService);
		//memberParsing.readCsvFile("C:/Temp/member.csv");
		
		//ToolParsing toolParsing = new ToolParsing(mainGroupService, subGroupService, toolService);
		//toolParsing.readCsvFile("C:/Temp/tool.csv");
		
		//stockStatusService.addMock();
		//tagService.addMock();
		//membershipService.updatePasswords();
		
		logger.info("Care4UManager  afterPropertiesSet... ");
	}
	
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
		logger.info("Care4UManager destroy... ");
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

}

package com.care4u.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.care4u.common.GlobalConstants;

@Service
public class LogWriterService {
	
	private final Logger mLog = LoggerFactory.getLogger(LogWriterService.class);
	
	public  void write(String directory, String phoneNumber, String log){
		phoneNumber = phoneNumber.replace(":", "_").trim();
		
		if(directory == null || directory.length() == 0) return;
		
		File directoryFile = new File(directory);
		if (!directoryFile.exists()) {
			directoryFile.mkdir();
		}
		
		if(phoneNumber == null || phoneNumber.length() <= 10) return;
		directoryFile = new File(directory + GlobalConstants.FILE_SEPERATOR + phoneNumber);
		if (!directoryFile.exists()) {
			directoryFile.mkdir();
		}
		
		String today = GlobalConstants.LOCAL_DATE_FORMATTER.format(LocalDateTime.now());
		String filename = directoryFile.getAbsolutePath() + GlobalConstants.FILE_SEPERATOR + phoneNumber + "_" + today +".log";
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(filename, true);
			bw = new BufferedWriter(fw);
			bw.write("[" + GlobalConstants.LOCAL_DATE_TIME_FORMAT.format(LocalDateTime.now()) + "] ");
			bw.write(log + GlobalConstants.LINE_SEPERATOR);
			bw.flush();
		} catch (Exception e) {
			mLog.error("Error!!! write error logfile : " + filename, e);
		} finally{
			if (bw != null) try{bw.close();} catch (IOException e) {}
			if (fw != null) try{fw.close();} catch (IOException e) {}
		}
	}
	
	public  void write(String directory, String log){
		if(directory == null || directory.length() == 0) return;

		File directoryFile = new File(GlobalConstants.LOG_HOME_DIRECTORY + GlobalConstants.FILE_SEPERATOR + directory);
		if (!directoryFile.exists()) {
			directoryFile.mkdir();
		}
		
		String today = GlobalConstants.LOCAL_DATE_FORMATTER.format(LocalDateTime.now());
		String filename = directoryFile.getAbsolutePath() + GlobalConstants.FILE_SEPERATOR + directory + "_" + today +".log";
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(filename, true);
			bw = new BufferedWriter(fw);
			bw.write("[" + GlobalConstants.LOCAL_DATE_TIME_FORMAT.format(LocalDateTime.now()) + "] ");
			bw.write(log + GlobalConstants.LINE_SEPERATOR);
			bw.flush();
		} catch (Exception e) {
			mLog.error("Error!!! write error logfile : " + filename, e);
		} finally{
			if (bw != null) try{bw.close();} catch (IOException e) {}
			if (fw != null) try{fw.close();} catch (IOException e) {}
		}
	}
}

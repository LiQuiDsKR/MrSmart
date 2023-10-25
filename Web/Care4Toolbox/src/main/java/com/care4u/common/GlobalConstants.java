package com.care4u.common;

import java.time.format.DateTimeFormatter;

public class GlobalConstants {

	public static final String LINE_SEPERATOR = System.getProperty("line.separator");
	public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	
	public static final DateTimeFormatter LOCAL_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	public static final DateTimeFormatter LOCAL_HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH");

	public static final boolean PROPHET_ON = false;
	public static final boolean CCTV_ON = false;
	public static final boolean DONGMOONENT_ON = false;
	public static final boolean NOISE_FILTER_ON = false;
	public static final boolean CURRENT_100A_ON = false;
	
	public static final String SERVER_LOG = "SERVER_CARE4U";
	
	public static final String APP_HOME_DIRECTORY = "C:\\Care4U";
	
	public static final String LOG_HOME_DIRECTORY = APP_HOME_DIRECTORY + FILE_SEPERATOR + "log";
	public static final String DEVICE_LOG_HOME_DIRECTORY = APP_HOME_DIRECTORY + FILE_SEPERATOR + "device_log";
	public static final String INFLUX_LOG_HOME_DIRECTORY = APP_HOME_DIRECTORY + FILE_SEPERATOR + "influx_log";
	public static final String DEVICE_MONITOR_HOME_DIRECTORY = APP_HOME_DIRECTORY + FILE_SEPERATOR + "device_monitor";
	public static final String DEVICE_MONITOR_ERROR_HOME_DIRECTORY = APP_HOME_DIRECTORY + FILE_SEPERATOR + "device_monitor_error";
	public static final String SERVER_STATUS_FILE_PATH = LOG_HOME_DIRECTORY + FILE_SEPERATOR + "server_status.json";

	public static final String CONFIG_HOME_DIRECTORY = APP_HOME_DIRECTORY + FILE_SEPERATOR + "config";
	public static final String SERVER_CONFIG_FILE_PATH = CONFIG_HOME_DIRECTORY + FILE_SEPERATOR + "server.properties";
	public static final String SENSOR_LIMIT_FILE_PATH = CONFIG_HOME_DIRECTORY + FILE_SEPERATOR + "sensor_limit.properties";
	
	
	public static final int NATURE_MODBUS_SERVER_PORT = 3239;
		
	public static final long DEVICE_ERROR_TIME_LIMIT = 30 * 60 * 1000;	//us
	public static final long COMMUNICATION_TIME_LIMIT = 10 * 1000;	//us
	public static final int COMMUNICATION_TIMEOUT = 3;	//seconds
	
	public static final int MAX_POOL_SIZE = 100;
	public static final int SUB_CONTROLLER_COUNT = 3;
	
	public static final int BUFFER_SIZE = 1024;
    public static final int SOCKET_BUFFER_SIZE = 10 * BUFFER_SIZE;
    
	public static final String APP_NAME = "Care4U SpringBoot v0.1";
	
}
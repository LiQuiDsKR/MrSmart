package com.care4u.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

public class Alarm {
	
	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "no")
	private int mNo;
	
	@Column(name = "deviceNo")
	private int mDeviceNo;
	
	@Column(name = "deviceName")
	private String mDeviceName;
	
	@Column(name = "sensorNo")
	private int mSensorNo;
	
	@Column(name = "sensorName")
	private String mSensorName;
	
	@Column(name = "sensorValue")
	private double mSensorValue;
	
	@Column(name = "sensorUnit")
	private String mSensorUnit;
	
	@Column(name = "sensorKqi")
	private int mSensorKqi;
	
	@Column(name = "etc")
	private String mEtc;
	
	@Column(name = "editingEmployeeNo")
	private int mEditingEmployeeNo;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "editingTime")
	private Date mEditingTime;

	public Alarm() {}
	
	public Alarm(int deviceNo, String deviceName, int sensorNo, String sensorName, double sensorValue, String sensorUnit, int sensorKqi, String etc) {
		mDeviceNo = deviceNo;
		mDeviceName = deviceName;
		mSensorNo = sensorNo;
		mSensorName = sensorName;
		mSensorValue = sensorValue;
		mSensorUnit = sensorUnit;
		mSensorKqi = sensorKqi;
		mEtc = etc;
		mEditingEmployeeNo = 1;
		mEditingTime = new Date();
	}

	public int getNo() {
		return mNo;
	}

	public void setNo(int no) {
		mNo = no;
	}

	public int getDeviceNo() {
		return mDeviceNo;
	}

	public void setDeviceNo(int deviceNo) {
		mDeviceNo = deviceNo;
	}

	public String getDeviceName() {
		return mDeviceName;
	}

	public void setDeviceName(String deviceName) {
		mDeviceName = deviceName;
	}

	public int getSensorNo() {
		return mSensorNo;
	}

	public void setSensorNo(int sensorNo) {
		mSensorNo = sensorNo;
	}

	public String getSensorName() {
		return mSensorName;
	}

	public void setSensorName(String sensorName) {
		mSensorName = sensorName;
	}

	public double getSensorValue() {
		return mSensorValue;
	}

	public void setSensorValue(double sensorValue) {
		mSensorValue = sensorValue;
	}

	public String getSensorUnit() {
		return mSensorUnit;
	}

	public void setSensorUnit(String sensorUnit) {
		mSensorUnit = sensorUnit;
	}

	public int getSensorKqi() {
		return mSensorKqi;
	}

	public void setSensorKqi(int sensorKqi) {
		mSensorKqi = sensorKqi;
	}

	public String getEtc() {
		return mEtc;
	}

	public void setEtc(String etc) {
		mEtc = etc;
	}

	public int getEditingEmployeeNo() {
		return mEditingEmployeeNo;
	}

	public void setEditingEmployeeNo(int editingEmployeeNo) {
		mEditingEmployeeNo = editingEmployeeNo;
	}

	public Date getEditingTime() {
		return mEditingTime;
	}

	public void setEditingTime(Date editingTime) {
		mEditingTime = editingTime;
	}
	
}
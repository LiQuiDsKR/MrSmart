package com.care4u.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.care4u.toolbox.group.main_group.MainGroupDto;
import com.care4u.toolbox.group.main_group.MainGroupService;
import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.group.sub_group.SubGroupService;

@Component
public class SummeryManager  implements InitializingBean, DisposableBean{
	private final Logger mLog = LoggerFactory.getLogger(SummeryManager.class);
	
	@Autowired private MainGroupService mMainGroupService;
	@Autowired private SubGroupService mSubGroupService;
	
	private Timer mTimer;
	private final TimerTask mTimerTask = new TimerTask(){
		@Override
		public void run() {
			processMainSummery();
		}
	};
	
	@Override
	public void afterPropertiesSet() throws Exception {
		mTimer = new Timer();
		mTimer.schedule(mTimerTask, 10 * 1000, 60 * 1000);
	}
	
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		mLog.info("destroy() : closing");
		
		if (mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
	}	
	
	private void processMainSummery() {

		

		
	}
	
	
}


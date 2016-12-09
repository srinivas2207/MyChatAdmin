package com.shree.mychatadmin;


import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.shree.mychatadmin.util.DatabaseUtil;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyChatAdminApplication extends Application{
	private static MyChatAdminApplication applicationContext = null;

	private static ThreadPoolExecutor threadPoolExecutor;


	public MyChatAdminApplication() {
		applicationContext = this;
	}
	
	public static ThreadPoolExecutor getThreadPoolExecutor() {
		return threadPoolExecutor;
	}
	
	public static MyChatAdminApplication getInstance() {
		if(applicationContext == null) {
			new MyChatAdminApplication();
		}			
		return applicationContext;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		_initializeTP();
		_initializeDB();
	}


	private void _initializeTP() {
		threadPoolExecutor = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(100),
				new MyChatRejectedExecutionHandler());
	}

	class MyChatRejectedExecutionHandler implements RejectedExecutionHandler {

		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		}
	}

	
	public void _initializeDB() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				DatabaseUtil.getInstance();
				//setup();
				return true;
			}			
		}.execute(null, null, null);
		
	}

}

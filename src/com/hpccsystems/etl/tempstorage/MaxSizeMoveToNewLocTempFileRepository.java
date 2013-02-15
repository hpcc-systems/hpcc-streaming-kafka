package com.hpccsystems.etl.tempstorage;

import org.springframework.beans.factory.InitializingBean;

//Once a configurable size is reached, repo is moved to a new location
//In this case the new location could be a path where files are scanned for upload to thor for ETL processing...
public class MaxSizeMoveToNewLocTempFileRepository  implements TempRepository, InitializingBean {

	private int maxSize;
	private String tempFilePath;
	private String maxSizeReachedMovePath;
	
	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}


	public String getTempFilePath() {
		return tempFilePath;
	}

	public void setTempFilePath(String tempFilePath) {
		this.tempFilePath = tempFilePath;
	}



	public String getMaxSizeReachedMovePath() {
		return maxSizeReachedMovePath;
	}

	public void setMaxSizeReachedMovePath(String maxSizeReachedMovePath) {
		this.maxSizeReachedMovePath = maxSizeReachedMovePath;
	}

	public void init() {
		//open existing or create new temp file	
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();		
	}

	public void save(byte[] data) {
		System.out.println(new String(data));
		//Save to file and flush
		//if file size reaches some max limit configured for this bean reached, then close file
		//move the file to the maxSizeReachedMovePath
	}	
}

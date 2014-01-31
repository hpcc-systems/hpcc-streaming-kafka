package com.hpccsystems.etl.event;

import org.springframework.context.ApplicationListener;

import com.hpccsystems.etl.tempstorage.TempRepository;

public class ETLDataReceivedListener implements ApplicationListener<LoadableDataReceivedEvent> {

	private TempRepository tempRepository;
	
	public TempRepository getTempRepository() {
		return tempRepository;
	}

	public void setTempRepository(TempRepository tempRepository) {
		this.tempRepository = tempRepository;
	}

	@Override
	public void onApplicationEvent(LoadableDataReceivedEvent event) {
		//When data is received, stor
		tempRepository.save(event.getData());
	}

}

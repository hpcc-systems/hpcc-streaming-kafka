package org.hpccsystems.streamapi.simulator;

import java.util.ArrayList;

public class VehicleSimulator {

	// String data_sample =
	// "F22387,1,,,0.0,,,null,,2,,1,null,1,1,5,20.0,null,,,0,null,31010601,0,31178761,121417742,6,17,0,"
	// +
	// "2013-07-04 08:55:17.0,2013-07-04 08:20:09.072362,34.0,32.0,979.0,980.0,35.0,455.0,6.0,140.0,231.0,46.0,136.0,"
	// +
	// "0.0,0.0,5.0,2.0,7.0,510.0,1.0,0.0,1.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,85.0,0.0,0.0,0.0,0.0,0.0,0.0";

	String dataSample = "<plate_code>,1,,,0.0,,,null,,2,,1,null,1,1,5,20.0,null,,,0,null,31010601,0,<latitude>,<longitude>,6,<speed>,0,"
			+ "<gps_time>,<recv_time>,34.0,32.0,979.0,980.0,35.0,455.0,6.0,140.0,231.0,46.0,136.0,"
			+ "0.0,0.0,5.0,2.0,7.0,510.0,1.0,0.0,1.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,85.0,0.0,0.0,0.0,0.0,0.0,0.0";

	private ArrayList<String> sampleData = new ArrayList<String>();

	private String plateCode;

	private int speed = 0;
	private int seconds = 0;
	private int minutes = 0;
	private int hours = 7;
	private int day = 10;
	private int month = 10;
	private int year = 2013;
	private int latitude = 34073312;
	private int longitude = 84281112;
	
	public VehicleSimulator(String plateCode) {
		this.plateCode = plateCode;
	}

	public void sample() {
		String result = dataSample.replaceFirst("<plate_code>", plateCode);
		result = result.replaceFirst("<gps_time>", getCurrentTime());
		result = result.replaceFirst("<recv_time>", getCurrentTime());
		result = result.replaceFirst("<speed>", String.valueOf(speed));
		result = result.replaceFirst("<latitude>", String.valueOf(latitude));
		result = result.replaceFirst("<longitude>", String.valueOf(longitude));

		sampleData.add(result);
	}

	public void acceleratePerSecond(int by) {
		addSecond();

		speed += by;
		latitude -= 10;
		longitude += 10;

		sample();
	}

	public void deacceleratePerSecond(int by) {
		addSecond();

		speed -= by;

		sample();
	}

	public void stop() {
		addSecond();
		
		while (speed > 0) {
			speed -= 5;
			addSecond();

			if (speed < 0) {
				speed = 0;
			}
			sample();
		}

		speed = 0;
	}

	public void passive(int seconds) {
       speed = 0;
       for (int i = 0; i < seconds; i++) {
    	   addSecond();
    	   sample();
       }
	}
	
	public void cruiseAtSameSpeed(int seconds) {
		for (int i = 0; i < seconds; i++) {
			addSecond();
			sample();
		}
	}
	
	
	public ArrayList<String> getData() {
		return new ArrayList<String>(sampleData);
	}

	public void clearData() {
		sampleData.clear();
	}

	private void addSecond() {
		seconds++;
		if (seconds > 59) {
			minutes++;
			seconds = 0;
			if (minutes > 59) {
				hours++;
				minutes = 0;
				if (hours >= 23) {
					day++;
					hours = 0;// Stop calculating at hours because we will not simulate for more than a day.
				}
			}
		}
	}

	/**
	 * Return the system date and time.
	 * @return
	 */
	
	private String getCurrentTime() {
		// Sample format "2013-07-04 08:55:17.0
		return year + "-" + month + "-" + day + " " + zeroPad(hours) + ":"
				+ zeroPad(minutes) + ":" + zeroPad(seconds) + ".0";
	}

	private String zeroPad(int value) {
		String svalue = String.valueOf(value);
		if (svalue.length() == 1) {
			return "0" + svalue;
		} else {
			return svalue;
		}
	}

}

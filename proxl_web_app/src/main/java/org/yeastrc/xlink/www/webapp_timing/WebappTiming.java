package org.yeastrc.xlink.www.webapp_timing;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

/**
 * Used for timing the web app
 *
 */
public class WebappTiming {

	

	private WebappTiming() { }
	public static WebappTiming getInstance( Logger logger ) { 
	
		WebappTiming instance = new WebappTiming();
		instance.logger = logger;
		return instance; 
	}

	private long startTime = System.nanoTime(); // default to start timing when instantiated
	
	private long prevMarkPoint = startTime;
	
	private long lastMarkPoint = startTime;
	
	private List<String> timings = new ArrayList<>();
	
	private Logger logger;
	
	/**
	 * @return list of timings
	 */
	public List<String> getTimings() {
		return timings;
	}
	
	public String getTimingsAsString() {
		
		StringBuilder timingsSB = new StringBuilder( 20000 ); 
		
		timingsSB.append( "\t TIMINGS FOR PAGE\t" );
		
		for ( String timing : timings ) {
			
			timingsSB.append( timing );
			timingsSB.append( "\t" );
		}
		
		String timingsString = timingsSB.toString();
		
		return timingsString;
	}
	
	
	/**
	 * reset start time to now, otherwise start time is when object is instantiated
	 */
	public void startTiming() {
		
		 startTime = System.nanoTime();
		 
	}
	
	/**
	 * log timings
	 * 
	 * @param label
	 */
	public void logTiming( ) {
		
		String timingsString = getTimingsAsString();
		
		logger.debug( timingsString );
	}
	
	
	/**
	 * @param label
	 */
	public void markPoint( String label ) {
		
		prevMarkPoint = lastMarkPoint;
		
		lastMarkPoint = System.nanoTime();
		
		String msg = "label: \t" + label + "\t"
				+ "Time from start point (in milliseconds) \t" + getTimeStartToMarkPointString() 
				+ "\tTime from prev mark point (in milliseconds) \t" + getTimePrevMarkPointToMarkPointString();

//		String msg = "label: \t" + label + "\t.  \t"
//				+ "Time from start point (in milliseconds) \t" + getTimeStartToMarkPointString() 
//				+ "\t, \tTime from prev mark point (in milliseconds) \t" + getTimePrevMarkPointToMarkPointString();

		timings.add(msg);
	}
	
	/**
	 * @return time since start in milliseconds
	 */
	public long getTimeStartToMarkPoint() {

		// ... the time being measured ...
		long estimatedTime = (lastMarkPoint - startTime) / 1000;

		return estimatedTime;
	}
	

	/**
	 * @return time since start in milliseconds
	 */
	public String getTimeStartToMarkPointString() {

		long estimatedTime = getTimeStartToMarkPoint ();

		NumberFormat numberFormat = NumberFormat.getInstance();
		
		String estimatedTimeString = numberFormat.format(estimatedTime);
		
		return estimatedTimeString;
	}
	
	
	/**
	 * @return time since prev mark in milliseconds
	 */
	public long getTimePrevMarkPointToMarkPoint() {


		// ... the time being measured ...
		long estimatedTime = (lastMarkPoint - prevMarkPoint) / 1000;

		return estimatedTime;
	}
	

	/**
	 * @return time since prev mark in milliseconds
	 */
	public String getTimePrevMarkPointToMarkPointString() {

		long estimatedTime = getTimePrevMarkPointToMarkPoint ();

		NumberFormat numberFormat = NumberFormat.getInstance();
		
		String estimatedTimeString = numberFormat.format(estimatedTime);
		
		return estimatedTimeString;
	}
	
//	To compare two nanoTime values
//
//	 long t0 = System.nanoTime();
//	 ...
//	 long t1 = System.nanoTime();
//	one should use t1 - t0 < 0, not t1 < t0, because of the possibility of numerical overflow.

	
}

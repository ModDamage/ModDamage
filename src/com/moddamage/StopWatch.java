package com.moddamage;

import java.util.HashMap;
import java.util.Map;

public class StopWatch {
	private Map<String, Long> starts;
	private Map<String, Long> stops;
	private Map<String, Double> elapses;
	
	public StopWatch()
	{
		starts = new HashMap<String, Long>();
		stops = new HashMap<String, Long>();
		elapses = new HashMap<String, Double>();
	}
	
	/**
	 * Starts a timer with the specified name.
	 * @param name of timer.
	 */
	public void start(String name)
	{
		starts.put(name, System.nanoTime());
		stops.remove(name);
	}
	
	/**
	 * Stops timer and returns the elapsed time in micro seconds.
	 * 
	 * @see #time(String)
	 * @param name of timer.
	 * @return time in micros that was elapses. Decimal form.
	 */
	public double stop(String name)
	{
		long curr = System.nanoTime();
		long start = starts.get(name);
		double elapse = (curr - start)/1000;

		stops.put(name, curr);
		
		if (elapses.containsKey(name))
			elapse += elapses.get(name);
		
		elapses.put(name, elapse);
		return time(name);
	}
	//TODO: Better javadoc.
	/**
	 * Returns the elapsed time in micro seconds in decimal form.
	 * @param name of timer
	 * @return double of how many micros elapsed or current elapsed time if timer was not stopped. Or 0 if no start was made.
	 */
	public double time(String name)
	{
		if (elapses.containsKey(name))
			return elapses.get(name); 
		else if (starts.containsKey(name))
			return (System.nanoTime() - starts.get(name))/1000; //Return current timing since we did not stop it yet.
		else
			return 0.0;
	}
	
	/**
	 * Resets the name timer.
	 * @param name of timer
	 */
	public void reset(String name)
	{
		starts.remove(name);
		stops.remove(name);
		elapses.remove(name);
	}
	
	/**
	 * Resets all timers.
	 */
	public void reset() {
		starts.clear();
		stops.clear();
		elapses.clear();
	}
}

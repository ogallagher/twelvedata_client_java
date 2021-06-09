package twelvedata_client_java;

import temp_fx_logger.System;

/**
 * Java client that pulls market data from the <a href="https://twelvedata.com/blog/first-introduction-getting-an-advantage-in-a-few-minutes">twelvedata API</a>.
 * 
 * @author Owen Gallagher
 * @since 9 June 2021
 * @version 0.0.1
 *
 */
public class TwelvedataClient {
	/**
	 * API key required to access twelvedata.
	 */
	private String key = null;
	
	public TwelvedataClient(String key) {
		this.key = key;
		
		System.out.println("init new " + this);
	}
	
	public String toString() {
		String out = 
			"TwelvedataClient(" + 
			this.key +
			')';
		
		return out;
	}
}

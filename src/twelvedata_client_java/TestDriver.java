package twelvedata_client_java;

import javafx.embed.swing.JFXPanel;

import temp_fx_logger.System;

/**
 * Test TwelvedataClient with a dummy parent application.
 * 
 * @author Owen Gallagher
 */
public class TestDriver {
	/**
	 * Prevents "Java Toolkit Not Initialized Error".
	 * I don't really get it, but an extra line doesn't do much harm anyway.
	 */
	@SuppressWarnings("unused") 
	private static JFXPanel dummyPanel = new JFXPanel();
	
	/**
	 * TestDriver entrypoint.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("running twelvedata_client_java.TestDriver");
		
		String apiKey = "api-key";
		TwelvedataClient tdclient = new TwelvedataClient(apiKey);
		
		System.out.println("twelvedata_client_java.TestDriver done");
	}
}
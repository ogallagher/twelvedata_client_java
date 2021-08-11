package ogallagher.twelvedata_client_java;

import java.time.LocalDate;

import javafx.application.Application;
import javafx.stage.Stage;
import ogallagher.temp_fx_logger.System;

/**
 * Test TwelvedataClient with a dummy parent application.
 * 
 * @author Owen Gallagher
 */
public class TestDriver {
	
	/**
	 * TestDriver entrypoint.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestDriverGUI.main(args);
	}
	
	/**
	 * Creating a hidden javafx application class behind the endpoint is an alternative to converting a project
	 * to a module in order to use javafx with java 9+.
	 * 
	 * @author Owen Gallagher
	 * @since 12 June 2021
	 */
	public static class TestDriverGUI extends Application {
		public static void main(String[] args) {
			launch(args);
			
			System.out.println("twelvedata_client_java.TestDriver done");
		}
		
		@Override
		public void start(Stage primaryStage) throws Exception {
			TwelvedataClient tdclient = new TwelvedataClient();
			
			boolean passed = true;
			int monthsBack = 30;
			LocalDate startDate = LocalDate.now().minusMonths(monthsBack);
			
			while (passed && monthsBack < 100) {
				if (tdclient.testFetchTimeSeries(startDate)) {
					passed = true;
					System.out.println("twelvedata client can fetch a time series from " + monthsBack + " months ago");
					
					monthsBack++;
					startDate = startDate.minusMonths(1);
				}
				else {
					passed = false;
					System.out.println("failed to fetch a time series from " + monthsBack + " months ago");
				}
			}
		}
	}
}

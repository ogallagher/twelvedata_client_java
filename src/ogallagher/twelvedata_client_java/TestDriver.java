package ogallagher.twelvedata_client_java;

import javafx.application.Application;
import javafx.stage.Stage;
import ogallagher.temp_fx_logger.System;
import ogallagher.twelvedata_client_java.TwelvedataInterface.BarInterval;

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
			
			if (tdclient.testFetchTimeSeries()) {
				System.out.println("twelvedata client can fetch a time series");
				
				String[] symbols = new String[] {
					"AAPL",
					"BAC"
				};
				String[] widths = new String[] {
					BarInterval.HR_1,
					BarInterval.DY_1,
					BarInterval.WK_1
				};
				
				System.out.println(
					"testing price history fetch for " + 
					symbols.length + " symbols and " + 
					widths.length + " bar widths"
				);
			}
		}
	}
}

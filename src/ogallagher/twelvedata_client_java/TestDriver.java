package ogallagher.twelvedata_client_java;

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
		}
	}
}

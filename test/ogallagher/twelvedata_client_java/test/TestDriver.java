package ogallagher.twelvedata_client_java.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import ogallagher.temp_fx_logger.System;
import ogallagher.twelvedata_client_java.TwelvedataClient;
import ogallagher.twelvedata_client_java.TwelvedataInterface.BarInterval;
import ogallagher.twelvedata_client_java.TwelvedataInterface.Failure;
import ogallagher.twelvedata_client_java.TwelvedataInterface.SecuritySet;
import ogallagher.twelvedata_client_java.TwelvedataInterface.TimeSeries;
import ogallagher.twelvedata_client_java.TwelvedataInterface.TimeSeries.TradeBar;

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
			// controller for easy access to each test in this method
			boolean doTest;
			
			// test symbol lookup
			
			doTest = true;
			if (doTest) {
				String symbolPrefix = "AA";
				SecuritySet securities = tdclient.symbolLookup(symbolPrefix, 10);
				if (securities != null) {
					System.out.println("symbol lookup success");
					
					for (SecuritySet.Security security : securities.data) {
						System.out.println(security);
					}
				}
				else {
					System.out.println("failed to lookup symbol " + symbolPrefix);
				}
			}
			
			// test price history fetch
			
			doTest = false;
			if (doTest) {
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
			
			// test different bar widths
			
			doTest = true;
			if (doTest) {
				LocalDateTime start = LocalDateTime.now().minusMonths(13);
				
				// 2hr
				TimeSeries bars = tdclient.fetchTimeSeries("AAPL", BarInterval.HR_2, start, start.plus(25*3, ChronoUnit.HOURS));
				if (!bars.isFailure()) {
					System.out.println("time series from " + start + ":");
					for (TradeBar bar : bars.values) {
						System.out.println("\t" + bar.toString());
					}
				}
				else {
					System.out.println("ERROR failed to fetch time series from " + start + ":\n" + ((Failure)bars));
				}
			}
		}
	}
}

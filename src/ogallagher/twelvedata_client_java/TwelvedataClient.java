package ogallagher.twelvedata_client_java;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gson.stream.JsonReader;

import ogallagher.temp_fx_logger.System;
import ogallagher.twelvedata_client_java.TwelvedataInterface.BarInterval;
import ogallagher.twelvedata_client_java.TwelvedataInterface.Failure;
import ogallagher.twelvedata_client_java.TwelvedataInterface.SecuritySet;
import ogallagher.twelvedata_client_java.TwelvedataInterface.TimeSeries;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Java client that pulls market data from the 
 * <a href="https://twelvedata.com/blog/first-introduction-getting-an-advantage-in-a-few-minutes">
 * 	twelvedata API
 * </a>.
 * 
 * @author Owen Gallagher
 * @since 9 June 2021
 * @version {@value TwelvedataClient#VERSION}
 *
 */
public class TwelvedataClient {
	public static final String VERSION = "0.2.0";
	
	public static final String API_PREFIX = "https://api.twelvedata.com";
	
	private final static Retrofit retrofit;
	private final static TwelvedataInterface api;
	
	public static final URL CONFIG_FILE = TwelvedataClient.class.getResource("resources/config.json");
	public static final String CONFIG_KEY_API_KEY = "api_key";
	private static HashMap<String,String> config = new HashMap<String,String>();
	
	private static Calendar calendar;
	
	/**
	 * Max api calls per minute, according to the free plan.
	 */
	private static final int MAX_CALLS_PER_MINUTE_FREE = 8;
	
	static {
		// define retrofit
		retrofit = new Retrofit.Builder()
			.baseUrl(API_PREFIX)
			.addConverterFactory(GsonConverterFactory.create())
			.build();
		
		// define api
		api = retrofit.create(TwelvedataInterface.class);
		
		// define config
		if (CONFIG_FILE != null) {
			File configFile = new File(CONFIG_FILE.getPath());
			
			if (!configFile.exists()) {
				System.out.println("ERROR: config file missing: " + configFile.getAbsolutePath());
			}
			else {
				System.out.println("reading config file " + configFile.getAbsolutePath());
				try {
					JsonReader reader = new JsonReader(new FileReader(configFile));
					reader.beginObject();
					
					while (reader.hasNext()) {
						String key = reader.nextName();
						String value = reader.nextString();
						
						config.put(key, value);
						System.out.println("config." + key + "=" + value);
					}
				}
				catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
		else {
			System.out.println("WARNING: twelvedata client has no config file");
		}
		
		// define calendar
		calendar = Calendar.getInstance();
	}
	
	/**
	 * API key required to access twelvedata.
	 */
	private String key = null;
	/**
	 * Max number of API calls per minute.
	 */
	private int maxCallsPerMinute;
	/**
	 * History of call timestamps, newest first.
	 */
	private LinkedList<Long> callHistory = new LinkedList<>();
	
	public TwelvedataClient() {
		this(config.get(CONFIG_KEY_API_KEY));
	}
	
	public TwelvedataClient(String key) {
		this.key = key;
		if (this.key == null || this.key.length() == 0) {
			System.out.println("WARNING: twelvedata client initialized without api key");
			this.key = null;
		}
		
		this.maxCallsPerMinute = MAX_CALLS_PER_MINUTE_FREE;
		
		System.out.println("init new " + this);
	}
	
	/**
	 * 
	 * @param symbol Security symbol.
	 * @param interval Trade bar width.
	 * @param startDate Start datetime.
	 * @param endDate End datetime.
	 * 
	 * @return {@link TimeSeries}. On failure, {@link TimeSeries#isFailure()} will return {@code true}.
	 */
	public TimeSeries fetchTimeSeries(String symbol, String interval, LocalDateTime startDate, LocalDateTime endDate) {
		if (callAllowed()) {
			if (startDate.isBefore(endDate)) {
				try {
					System.out.println("DEBUG fetching time series");
					Response<TimeSeries> res = api
						.timeSeries(symbol, interval, startDate.toString(), endDate.toString(), key)
						.execute();
					
					TimeSeries out;
					
					if (res != null) {
						if (res.isSuccessful()) {
							TimeSeries timeSeries = res.body();
							
							if (!timeSeries.isFailure()) {
								System.out.println("DEBUG fetched time series of length " + timeSeries.values.size());
							}
							
							out = timeSeries;
						}
						else {
							out = new TimeSeries(res.code(), res.errorBody().string());
						}
					}
					else {
						out = new TimeSeries(Failure.ErrorCode.NULL_RESPONSE, "http api response is null");
					}
					
					callHistory.addFirst(new Date().getTime());
					return out;
				}
				catch (IOException e) {
					return new TimeSeries(Failure.ErrorCode.NO_COMMS, e.getMessage());
				}
			}
			else {
				return new TimeSeries(Failure.ErrorCode.INVALID_DATES, "start " + startDate + " must be less than end " + endDate);
			}
		}
		else {
			return new TimeSeries(Failure.ErrorCode.CALL_LIMIT, "ERROR hit max api call limit of " + maxCallsPerMinute + " per minute");
		}
	}
	
	/**
	 * 
	 * @param symbol Security symbol.
	 * @param maxResults Max number of results. Will be constrained to be within the range {@code [1..120]}.
	 * 
	 * @return {@link SecuritySet} or {@code null}.
	 */
	public SecuritySet symbolLookup(String symbol, int maxResults) {
		if (maxResults < 1) {
			maxResults = 1;
		}
		else if (maxResults > 120) {
			maxResults = 120;
		}
		
		if (callAllowed()) {
			System.out.println("performing symbol lookup for " + symbol);
			try {
				Response<SecuritySet> res = api
					.symbolSearch(symbol, maxResults)
					.execute();
				
				SecuritySet out = null;
				
				if (res != null) {
					if (res.isSuccessful()) {
						SecuritySet securitySet = res.body();
						
						if (!securitySet.isFailure()) {
							System.out.println("fetched " + securitySet.data.size() + " matching securities");
							out = securitySet;
						}
						else {
							System.out.println(((Failure) securitySet).toString());
						}
					}
					else {
						System.out.println(res.errorBody().string());
					}
				}
				else {
					System.out.println("http api response is null");
				}
				
				return out;
			} 
			catch (IOException e) {
				System.out.println(e.getMessage());
				return null;
			}
		}
		else {
			System.out.println("hit max api call limit of " + maxCallsPerMinute + " per minute");
			return null;
		}
	}
	
	public boolean callAllowed() {
		int n = callHistory.size();
		
		if (n >= maxCallsPerMinute) {
			int oldestIdx = maxCallsPerMinute-1;
			long oldest = callHistory.get(oldestIdx);
			
			// see https://stackoverflow.com/a/11882964/10200417
			calendar.setTime(new Date());
			calendar.add(Calendar.MINUTE, -1);
			if (oldest > calendar.getTime().getTime()) {
				// too many calls within the last minute; call not allowed
				return false;
			}
			else {
				// clear old calls
				int oldCalls = n - oldestIdx;
				for (int i=0; i<oldCalls; i++) {
					callHistory.removeLast();
				}
				
				return true;
			}
		}
		else {
			return true;
		}
	}
	
	/**
	 * Attempts to get one week of daily close prices for AAPL.
	 * 
	 * @return {@code true} if {@link TimeSeries#isFailure() TimeSeries.isFailure() == false}.
	 */
	public boolean testFetchTimeSeries(LocalDate from) {
		if (from == null) {
			from = LocalDate.now().minusDays(7);
		}
		
		final String TEST_SYMBOL = "AAPL";
		final String TEST_INTERVAL = BarInterval.DY_1;
		final LocalDate testStartDate = from;
		final LocalDate TEST_END_DATE = testStartDate.plusDays(7);
		System.out.println("expecting " + Period.between(testStartDate, TEST_END_DATE).getDays() + " x " + TEST_INTERVAL);
		
		TimeSeries timeSeries = fetchTimeSeries(
				TEST_SYMBOL, 
				TEST_INTERVAL, 
				LocalDateTime.of(testStartDate, LocalTime.MIDNIGHT), 
				LocalDateTime.of(TEST_END_DATE, LocalTime.MIDNIGHT));
		if (!timeSeries.isFailure()) {
			System.out.println("timeSeries.meta = " + timeSeries.meta);
			for (int i=0; i<timeSeries.values.size(); i++) {
				System.out.println("timeSeries.values[" + i + "] = " + timeSeries.values.get(i));
			}
			return true;
		}
		else {
			System.out.println(((Failure)timeSeries).toString());
			return false;
		}
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public int getMaxCallsPerMinute() {
		return maxCallsPerMinute;
	}
	
	public String toString() {
		String out = 
			"TwelvedataClient(" + 
			this.key +
			")";
		
		return out;
	}
}

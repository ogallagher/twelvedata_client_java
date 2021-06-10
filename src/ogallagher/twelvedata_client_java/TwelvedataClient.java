package ogallagher.twelvedata_client_java;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;

import com.google.gson.stream.JsonReader;

import ogallagher.temp_fx_logger.System;
import ogallagher.twelvedata_client_java.TwelvedataInterface.BarInterval;
import ogallagher.twelvedata_client_java.TwelvedataInterface.Failure;
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
	public static final String VERSION = "0.0.1";
	
	public static final String API_PREFIX = "https://api.twelvedata.com";
	
	private final static Retrofit retrofit;
	private final static TwelvedataInterface api;
	
	public static final String CONFIG_FILE = TwelvedataClient.class.getResource("resources/config.json").getPath();
	public static final String CONFIG_KEY_API_KEY = "api_key";
	private static HashMap<String,String> config = new HashMap<String,String>();
	
	static {
		// define retrofit
		retrofit = new Retrofit.Builder()
			.baseUrl(API_PREFIX)
			.addConverterFactory(GsonConverterFactory.create())
			.build();
		
		// define api
		api = retrofit.create(TwelvedataInterface.class);
		
		// define config
		File configFile = new File(CONFIG_FILE);
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
	
	/**
	 * API key required to access twelvedata.
	 */
	private String key = null;
	
	public TwelvedataClient() {
		this(config.get(CONFIG_KEY_API_KEY));
	}
	
	public TwelvedataClient(String key) {
		this.key = key;
		if (this.key == null || this.key.length() == 0) {
			System.out.println("WARNING: twelvedata client initialized without api key");
			this.key = null;
		}
		else if (!testFetchTimeSeries()) {
			System.out.println("ERROR: twelvedata client failed to connect to api endpoint");
		}
		
		System.out.println("init new " + this);
	}
	
	/**
	 * 
	 * @param symbol Security symbol.
	 * @param interval Trade bar width.
	 * @param startDate Start date.
	 * @param endDate End date.
	 * 
	 * @return {@link TimeSeries} or <code>null<code>.
	 */
	public TimeSeries fetchTimeSeries(String symbol, String interval, LocalDate startDate, LocalDate endDate) {
		try {
			System.out.println("fetching time series");
			Response<TimeSeries> res = api
				.timeSeries(symbol, interval, startDate.toString(), startDate.toString(), key)
				.execute();
			
			if (res != null) {
				if (res.isSuccessful()) {
					TimeSeries timeSeries = res.body();
					
					if (!timeSeries.isFailure()) {
						System.out.println("fetched time series of length " + timeSeries.values.size());
						return timeSeries;
					}
					else {
						System.out.println(((Failure) timeSeries).toString());
						System.out.println("api error");
						return null;
					}
				}
				else {
					System.out.println(res.errorBody().string());
					return null;
				}
			}
			else {
				System.out.println("http api response is null");
				return null;
			}
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	private boolean testFetchTimeSeries() {
		final String TEST_SYMBOL = "AAPL";
		final String TEST_INTERVAL = BarInterval.DY_1;
		final LocalDate TEST_START_DATE = LocalDate.of(2020, 1, 1);
		final LocalDate TEST_END_DATE = LocalDate.of(2020, 1, 8);
		
		TimeSeries timeSeries = fetchTimeSeries(TEST_SYMBOL, TEST_INTERVAL, TEST_START_DATE, TEST_END_DATE);
		if (timeSeries != null) {
			System.out.println("timeSeries.meta = " + timeSeries.meta);
			System.out.println("timeSeries.values[0] = " + timeSeries.values.get(0));
			return true;
		}
		else {
			return false;
		}
	}
	
	public String toString() {
		String out = 
			"TwelvedataClient(" + 
			this.key +
			')';
		
		return out;
	}
}

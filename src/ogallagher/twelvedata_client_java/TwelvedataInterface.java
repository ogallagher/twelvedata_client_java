package ogallagher.twelvedata_client_java;

import java.time.LocalDateTime;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Use <a href="https://square.github.io/retrofit">retrofit</a> to convert twelvedata endpoint responses to typed and
 * structured Java objects.
 * 
 * @author Owen Gallagher
 *
 */
public interface TwelvedataInterface {
	/**
	 * Fetch a trade bar sequence for a given security over a specified interval.
	 * 
	 * @param symbol Security symbol (ex. ABC).
	 * @param interval Width of each trade bar.
	 * @param startDate When to begin fetching trade bars.
	 * @param endDate When to stop fetching trade bars.
	 * @param apiKey twelvedata API key.
	 * 
	 * @return trade bars in json format.
	 */
	@GET(
		"time_series?&format=json"
	)
	Call<TimeSeries> timeSeries(
		@Query("symbol") String symbol,
		@Query("interval") String interval,
		@Query("start_date") String startDate,
		@Query("end_date") String endDate,
		@Query("apikey") String apiKey
	);
	
	/**
	 * Perform a security symbol lookup to retrieve important identifying information.
	 * 
	 * @param symbol Security symbol (ex. ABC).
	 * @param outputSize Maximum number of results. Must be less or equal to 120.
	 * 
	 * @return Matching securities in json format.
	 */
	@GET(
		"symbol_search"
	)
	Call<SecuritySet> symbolSearch(
		@Query("symbol") String symbol,
		@Query("outputsize") int outputSize
	);
	
	public class BarInterval {
		public static final String 
			MIN_1 = "1min",
			MIN_5 = "5min",
			MIN_15 = "15min", 
			MIN_30 = "30min", 
			MIN_45 = "45min",
			HR_1 = "1h", 
			HR_2 = "2h", 
			HR_4 = "4h", 
			HR_8 = "8h", 
			DY_1 = "1day", 
			WK_1 = "1week", 
			MO_1 = "1month";
		
		/**
		 * Calculate a new datetime as {@code base + barWidth * offset}.
		 * 
		 * @param base Base datetime to offset from.
		 * @param barWidth The width of the bar, corresponding to a time duration.
		 * @param offset Number of bars away from the base.
		 * 
		 * @return Calculated datetime, offset bars from {@code base}, or {@code null} if the given
		 * bar width is not supported.
		 */
		public static LocalDateTime offsetBars(LocalDateTime base, String barWidth, long offset) {
			switch (barWidth) {
				// hours
				case BarInterval.HR_1:
					return base.plusHours(offset);
					
				case BarInterval.HR_2:
					return base.plusHours(2*offset);
					
				case BarInterval.HR_4:
					return base.plusHours(4*offset);
					
				case BarInterval.HR_8:
					return base.plusHours(8*offset);
				
				// days
				case BarInterval.DY_1:
					return base.plusDays(offset);
					
				// minutes
				case BarInterval.MIN_1:
					return base.plusMinutes(offset);
					
				case BarInterval.MIN_5:
					return base.plusMinutes(5*offset);
					
				case BarInterval.MIN_15:
					return base.plusMinutes(15*offset);
					
				case BarInterval.MIN_30:
					return base.plusMinutes(30*offset);
					
				case BarInterval.MIN_45:
					return base.plusMinutes(45*offset);
					
				// weeks
				case BarInterval.WK_1:
					return base.plusWeeks(offset);
					
				// months
				case BarInterval.MO_1:
					return base.plusMonths(offset);
					
				default:
					System.out.println("WARNING: unimplemented bar width " + barWidth + " for datetime offsets");
					return null;
			}
		}
	}
	
	public class Failure {
		public Integer code = null;
		public String message = null;
		
		public Failure() {}
		
		public Failure(int code, String message) {
			this.code = code;
			this.message = message;
		}
		
		/**
		 * @return {@code true} if this instance is a true failure, where {@code code != null}.
		 */
		public boolean isFailure() {
			return code != null;
		}
		
		public String toString() {
			return "TimeSeries.Failure("
				+ code + ","
				+ message 
				+ ")";
		}
		
		/**
		 * Known API failure error codes.
		 * 
		 * @author Owen Gallagher
		 * @since 2021-08-28
		 */
		public class ErrorCode {
			/**
			 * No data available between the specified datetimes.
			 */
			public static final int NO_BARS = 400;
			/**
			 * Incorrect or missing API key.
			 */
			public static final int API_KEY = 401;
			
			// negative codes are internal to this package
			
			/**
			 * No response from the server.
			 */
			public static final int NULL_RESPONSE = -1;
			/**
			 * Unable to make a connection to the server.
			 */
			public static final int NO_COMMS = -2;
			/**
			 * Invalid start and end dates (ex. end < start).
			 */
			public static final int INVALID_DATES = -3;
			/**
			 * Limit of max API calls per minute exceeded.
			 */
			public static final int CALL_LIMIT = -4;
		}
	}
	
	/**
	 * A chronological series of historical trade bars over time.
	 * 
	 * Note that trade bars in the series are not explicitly ordered here, but are chronologically descending
	 * by default as returned from the twelvedata api.
	 * 
	 * @author Owen Gallagher
	 *
	 */
	public class TimeSeries extends Failure {
		public Meta meta = null;
		public ArrayList<TradeBar> values = null;
		
		/**
		 * Failure constructor; calls {@link Failure#Failure(int, String)}.
		 * 
		 * @param errorCode Error code.
		 * @param message Error message.
		 */
		public TimeSeries(int errorCode, String message) {
			super(errorCode, message);
		}
		
		public class Meta {
			public String symbol;
			public String interval;
			public String exchange_timezone;
			public String exchange;
			public String type;
			
			public String toString() {
				return "TimeSeries.Meta("
					+ symbol + ","
					+ interval + ","
					+ exchange_timezone + ","
					+ exchange + ","
					+ type
					+ ")";
			}
		}
		
		public class TradeBar {
			public String datetime;
			public float open;
			public float high;
			public float low;
			public float close;
			public long volume;
			
			public String toString() {
				return "TradeBar("
					+ datetime + ","
					+ open + ","
					+ high + ","
					+ low + ","
					+ close + ","
					+ volume
					+ ")";
			}
		}
	}
	
	public class SecurityType {
		public static final String
			COMMON_STOCK = "Common Stock",
			ETF = "ETF";
	}
	
	public class SecuritySet extends Failure {
		public ArrayList<Security> data = null;
		
		public String status;
		
		public class Security {
			/**
			 * Security symbol.
			 */
			public String symbol;
			/**
			 * Long name.
			 */
			public String instrument_name;
			/**
			 * Exchange symbol, ex. {@code NYSE}.
			 */
			public String exchange;
			/**
			 * Exchange timezone, ex. {@code America/New York}.
			 */
			public String exchange_timezone;
			/**
			 * Security type, ex. {@code Common Stock}, {@code ETF}.
			 */
			public String instrument_type;
			/**
			 * Security home country, ex. {@code United States}.
			 */
			public String country;
			/**
			 * Security quote currency, ex. {@code USD}.
			 */
			public String currency;
			
			@Override
			public String toString() {
				return "Security(symbol=" + symbol + ", exchange=" + exchange + ")";
			}
		}
	}
}

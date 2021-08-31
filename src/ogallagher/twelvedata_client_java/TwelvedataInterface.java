package ogallagher.twelvedata_client_java;

import java.time.Duration;
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
	 * Note that when specifying a datetime boundary and number of bars, you have to use {@code endDate}
	 * with {@code outputSize} to get desired results. Using {@code startDate} will cause the datetime to
	 * just be ignored.
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
	 * Fetch a trade bar sequence for a given security, ending at {@code endDate} and containing 
	 * {@code outputSize} bars.
	 * 
	 * @param symbol Security symbol (ex. ABC).
	 * @param interval Width of each trade bar.
	 * @param endDate When to stop fetching trade bars.
	 * @param outputSize Number of trade bars to include.
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
		@Query("end_date") String endDate,
		@Query("outputsize") int outputSize,
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
		 * An enumeration encapsulating the mapping between bar width/interval strings and {@link Duration durations}.
		 * 
		 * @author Owen Gallagher
		 * @since 2021-08-30
		 */
		protected static enum IntervalDuration {
			MIN_1(BarInterval.MIN_1),
			MIN_5(BarInterval.MIN_5),
			MIN_15(BarInterval.MIN_15),
			MIN_30(BarInterval.MIN_30),
			MIN_45(BarInterval.MIN_45),
			HR_1(BarInterval.HR_1),
			HR_2(BarInterval.HR_2),
			HR_4(BarInterval.HR_4),
			HR_8(BarInterval.HR_8),
			DY_1(BarInterval.DY_1),
			WK_1(BarInterval.WK_1),
			MO_1(BarInterval.MO_1);
			
			private String string;
			private Duration duration;
			
			private IntervalDuration(String string) {
				this.string = string;
				
				switch (string) {
					case BarInterval.MIN_1:
						duration = Duration.ofMinutes(1);
						break;
						
					case BarInterval.MIN_5:
						duration = Duration.ofMinutes(5);
						break;
						
					case BarInterval.MIN_15:
						duration = Duration.ofMinutes(15);
						break;
						
					case BarInterval.MIN_30:
						duration = Duration.ofMinutes(30);
						break;
						
					case BarInterval.MIN_45:
						duration = Duration.ofMinutes(45);
						break;
						
					case BarInterval.HR_1:
						duration = Duration.ofHours(1);
						break;
						
					case BarInterval.HR_2:
						duration = Duration.ofHours(2);
						break;
						
					case BarInterval.HR_4:
						duration = Duration.ofHours(4);
						break;
						
					case BarInterval.HR_8:
						duration = Duration.ofHours(8);
						break;
					
					case BarInterval.DY_1:
						duration = Duration.ofDays(1);
						break;
						
					case BarInterval.WK_1:
						duration = Duration.ofDays(7);
						break;
						
					case BarInterval.MO_1:
						duration = Duration.ofDays(30);
						break;
						
					default:
						System.out.println("ERROR: unknown bar width " + string);
						duration = null;
						break;
				}
			}
			
			/**
			 * @return String value for this bar width.
			 */
			@Override
			public String toString() {
				return string;
			}
			
			/**
			 * @return {@link Duration} value for this bar width.
			 */
			public Duration getDuration() {
				return duration;
			}
		}
		
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
		
		/**
		 * Similar to {@link #offsetBars(LocalDateTime, String, long) offsetBars}, but with an additional constraint
		 * that ensures the number of trade bars is at least {@code abs(offsetMin)}.
		 * 
		 * @throws UnsupportedOperationException This method is not yet implemented.
		 * 
		 * @param base Base datetime to offset from.
		 * @param barWidth The width of a bar, corresponding to a time duration.
		 * @param offsetMin Estimated/minimum number of bars away from the base.
		 * @param exchange The exchange to whose asset these bars belong.
		 * 
		 * @return Calculated datetime, displaced from {@code base}.
		 */
		@SuppressWarnings("unused")
		public static LocalDateTime offsetBarsCounted(LocalDateTime base, String barWidth, long offsetMin, String exchange) throws UnsupportedOperationException {
			Duration barDuration = IntervalDuration.valueOf(barWidth).getDuration();
			Duration offsetDuration = barDuration.multipliedBy(offsetMin);
			
			// determine preliminary dest date
			LocalDateTime dest = base.plus(offsetDuration);
			
			// count without accounting for holidays, weekends, closures
			long barCount = Math.abs(offsetMin);
			
			// subtract holidays, weekends, closures
			
			// add back needed bars
			
			// return new dest
			
			throw new UnsupportedOperationException("offsetBarsCounted not yet implemented");
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

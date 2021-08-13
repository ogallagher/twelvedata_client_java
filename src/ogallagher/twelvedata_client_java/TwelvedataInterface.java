package ogallagher.twelvedata_client_java;

import java.time.LocalDateTime;
import java.util.ArrayList;

import ogallagher.twelvedata_client_java.TwelvedataInterface.BarInterval;
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
		
		public static LocalDateTime offsetBars(LocalDateTime base, String barWidth, long offset) {
			switch (barWidth) {
				case BarInterval.HR_1:
					return base.plusHours(offset);
					
				case BarInterval.HR_2:
					return base.plusHours(2*offset);
					
				case BarInterval.HR_4:
					return base.plusHours(4*offset);
					
				case BarInterval.HR_8:
					return base.plusHours(8*offset);
					
				case BarInterval.DY_1:
					return base.plusDays(offset);
					
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
		
		public boolean isFailure() {
			return code != null;
		}
		
		public String toString() {
			return "TimeSeries.Failure("
				+ code + ","
				+ message 
				+ ")";
		}
	}
	
	/**
	 * A chronological series of historical trade bars over time.
	 * 
	 * @author Owen Gallagher
	 *
	 */
	public class TimeSeries extends Failure {
		public Meta meta = null;
		public ArrayList<TradeBar> values = null;
		
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
			public int volume;
			
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

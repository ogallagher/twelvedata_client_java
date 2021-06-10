package ogallagher.twelvedata_client_java;

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
		@Query("startDate") String startDate,
		@Query("endDate") String endDate,
		@Query("apikey") String apiKey
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
	}
	
	public class Failure {
		public Integer code = null;
		public String message = null;
		
		public Failure() {}
		
		public Failure(int code, String message) {
			this.code = code;
			this.message = message;
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
		
		public boolean isFailure() {
			return code != null;
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
}

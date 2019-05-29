package data;

/*This class is a candlestickobject, it contains the date and OHLC data 
 * 
 */
public class Candlestick {
	private double open, high, low, close;
	private String date;
	private double profit;

	public Candlestick(String date, double open, double high, double low, double close) {
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
	}
	/** Returns the Date of the candlestick*/
	public String getDate() {
		return date;
	}

	/**Returns the value of a part of the candle, open=0, high=1, etc. Profit = 4*/
	public double getValue(int index) {
		switch (index) {
		case 0:
			return open;
		case 1:
			return high;
		case 2:
			return low;
		case 3:
			return close;
		case 4:
			return profit;
		}
		return (Double) null;
	}

	/** Returns the string representation of a candle*/
	public String toString() {
		return "[DATE:" + date + "][OPEN:" + open + "][HIGH:" + high + "][LOW:" + low + "][CLOSE:" + close + "]";
	}

	/**Sets the candles profit if a trade would have been made days days ago.*/
	public void addProfit(double profit) {
		this.profit = profit;
	}
}

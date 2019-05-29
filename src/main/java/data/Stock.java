package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This object represents a stock. It contains all the days' values. 
 * It also contains the candles scaled value. These values are better for a neural network to understand.
 */

public class Stock {
	private ArrayList<Candlestick> candles;
	public String symbol;
	private int days, buyingTime, sellingTime;
	
	public Stock(String symbol, ArrayList<Candlestick> candles, int days, int buyingTime, int sellingTime) {
		this.symbol = symbol;
		this.days = days;
		this.buyingTime = buyingTime;
		this.sellingTime = sellingTime;
		this.candles = candles; // An array of all datapoints in the dataset
		generateProfit(days, buyingTime, sellingTime);
	}
	
	/**Returns the number of candles in the array*/
	public int length() {return candles.size();}
	
	/**Adds a candlestick to the candlestick array*/
	private void addCandle(String date, double open, double high, double low, double close) {
		candles.add(new Candlestick(date, open, high, low, close));
		generateProfit(days, buyingTime, sellingTime);

	}
	
	/**Adds an array of candles to the candlestick array*/
	private void addCandles(ArrayList<Candlestick> candleList) {
		for (Candlestick c : candleList) {
			candles.add(c);
		}
		generateProfit(days, buyingTime, sellingTime);
	}

	/**Adds the profit value to the candles. Buying and selling times refer to the OHLC*/
	private void generateProfit(int days, int buyingTime, int sellingTime) {
		for (int i = 0; i < candles.size()-days; i++) {
			double profit=candles.get(i+days).getValue(sellingTime) / candles.get(i).getValue(buyingTime);
			candles.get(i).addProfit(profit);
			//scaledCandles.get(i).addProfit(profit);
		}
	}
	
	public Candlestick getCandle(int index) {
		return candles.get(index);
	}
	
}

package data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class DataManager {
	private ArrayList<Stock> trainingData;
	private ArrayList<Stock> validationData;
	private int years;
	private int days, buyingTime, sellingTime;

	public DataManager(int days, int buyingTime, int sellingTime, int years) {
		//This is just to create the red text before the debuggning text
		try {
			yahoofinance.Stock s = YahooFinance.get("AAPL");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		trainingData = new ArrayList<Stock>();
		validationData = new ArrayList<Stock>();

		this.buyingTime = buyingTime;
		this.sellingTime = sellingTime;
		this.days = days;
		this.years = years;
	}

	public void addStock(String symbol, boolean training) {
		System.out.print("Adding stock: " + symbol + ". ");
		ArrayList<Candlestick> candles = new ArrayList<Candlestick>(); // The candles that will be added to the stock

		File folder = new File("./StockData/"); // The folder where the data is stored.
		File[] listOfFiles = folder.listFiles();
		boolean exists = false;
		boolean upToDate = false;

		// First we see if the file exists and if so import the data
		for (File f : listOfFiles) {
			if (f.getPath().equals("./StockData/" + symbol + ".txt")||f.getPath().equals(".\\StockData\\" + symbol + ".txt")) {
				exists = true;
				candles = importDataFromFile("./StockData/" + symbol + ".txt");
				break;
			}
		}

		// If it exists we check if it is up to date
		if (exists) {
			System.out.print("File exists. ");
			// Converting date to a easily comparable thing
			String[] str = candles.get(candles.size() - 1).getDate().split("-");
			long candleDateSize = Integer.parseInt(str[0]) * 365 + Integer.parseInt(str[1]) * 12
					+ Integer.parseInt(str[2]);
			str = LocalDate.now().toString().split("-");
			long nowDateSize = Integer.parseInt(str[0]) * 365 + Integer.parseInt(str[1]) * 12
					+ Integer.parseInt(str[2]);

			if (candleDateSize+1 >= nowDateSize) {
				upToDate = true;
			}
			else {
				System.out.print("Outdated. ");
			}

		}
		else System.out.print("File does not exist. ");
		// if the file is either non existant or not up to date
		if (!exists || !upToDate) {
			System.out.print("Downloading. ");
			candles = importDataFromYahoo(symbol);
			storeData(symbol, candles);
		}
		
		if (training) {
			trainingData.add(new Stock(symbol, candles, days, buyingTime, buyingTime));
		} else {
			validationData.add(new Stock(symbol, candles, days, buyingTime, buyingTime));
		}
		System.out.println();
	}

	private void storeData(String symbol, ArrayList<Candlestick> candles) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("./StockData/" + symbol + ".txt", false));
			writer.close();
			// Open given file in append mode.
			writer = new BufferedWriter(new FileWriter("./StockData/" + symbol + ".txt", true));
			for (int c = 0;c<candles.size(); c++) {
				String str = candles.get(c).getDate() + ",";
				
				for (int i = 0; i < 4; i++) {
					str += candles.get(c).getValue(i);
					if(i!=3) str+=",";
				}
				if(!(c==candles.size()-1))str+="\n";
				writer.write(str);
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("exception occoured" + e);
		}

	}

	private ArrayList<Candlestick> importDataFromYahoo(String symbol) {
		ArrayList<Candlestick> candles = new ArrayList<Candlestick>();

		List<HistoricalQuote> data = null;
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();

		from.add(Calendar.YEAR, -1 * years);
		boolean downloaded = false;

		try {
			data = YahooFinance.get(symbol).getHistory(from, to, Interval.DAILY);
			System.out.print(" download successfull!");
			downloaded = true;
		} catch (Exception e) {
			System.out.print(" download failed...");
			System.out.println(e);
			downloaded = false;
		}
		if (downloaded && data != null) {
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			for (HistoricalQuote h : data) {
				if (!h.equals(null)) {
					candles.add(new Candlestick(format1.format(h.getDate().getTime()), h.getOpen().doubleValue(),
							h.getHigh().doubleValue(), h.getLow().doubleValue(), h.getClose().doubleValue()));
				}
			}
		}
		ArrayList<Candlestick> newCandles = new ArrayList<Candlestick>();
		for (Candlestick c : candles) {
			if (c != null) {
				newCandles.add(c);
			}
		}
		candles = newCandles;
		return candles;
	}

	/** Imports all the datapoints from a file separated by commas and \n */
	private ArrayList<Candlestick> importDataFromFile(String filename) {
		ArrayList<Candlestick> candles = new ArrayList<Candlestick>();
		File file = new File(filename);
		Scanner scan = null;

		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int i = 0;
		while (scan.hasNextLine()) {
			i++;
			String line = scan.nextLine();

			ArrayList<String> data = new ArrayList<String>();
			for (String s : line.split(",")) {
				data.add(s);
			}
			candles.add(new Candlestick(data.get(0), Double.valueOf(data.get(1)), Double.valueOf(data.get(2)),
					Double.valueOf(data.get(3)), Double.valueOf(data.get(4))));
		}
		return candles;
	}

	public ArrayList<Stock> getTrainingData() {
		return trainingData;
	}

	public ArrayList<Stock> getValidationData() {
		return validationData;
	}

}

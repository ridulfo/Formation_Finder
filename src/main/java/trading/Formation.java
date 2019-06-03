package trading;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import data.Candlestick;
import data.Stock;
import trading.Formation.ThreeState;

public class Formation implements Runnable {
	private ArrayList<ThreeState> genes;
	private int nCandles;
	private double average = 0, accumulation = 1, winningTrades = 0;
	public static double cost;
	private ArrayList<Double> trades;
	public static ArrayList<Stock> stocks;

	/**This is a custom three-value boolean*/
	public enum ThreeState {
		HIGHER, LOWER, NONE
	};

	public Formation(int nCandles, boolean random, double[] geneDistribution) {
		this.nCandles = nCandles;
		genes = new ArrayList<ThreeState>();
		trades = new ArrayList<Double>();

		// populate the genes with null genes (to begin with)
		for (int i = 0; i < 8 * Math.pow(nCandles - 1, 2) + 8 * (nCandles - 1); i++) {
			genes.add(ThreeState.NONE);
		}
		// only used in the original creatures
		if (random)
			setRandom(geneDistribution);

	}
	/**This constructor is used when the formation that is being created is not a random one.*/
	public Formation(int nCandles) {
		this.nCandles = nCandles;
		genes = new ArrayList<ThreeState>();
		trades = new ArrayList<Double>();

		// populate the genes with null genes (to begin with)
		for (int i = 0; i < 8 * Math.pow(nCandles - 1, 2) + 8 * (nCandles - 1); i++) {
			genes.add(ThreeState.NONE);
		}
	}

	/**
	 * Here the genes are set at random. This is used the first time as to create a
	 * random formation. This method takes an array of doubles as a parameter. This
	 * array defines the probability a gene will be found in the formations genes.
	 * This was done so that one could decide how probable every gene would be.
	 */
	public void setRandom(double[] geneDistribution) {
		if (geneDistribution.length != 2 || geneDistribution[0] < 0 || geneDistribution[0] > 1
				|| geneDistribution[1] < 0 || geneDistribution[1] > 1) {
			throw new java.lang.Error("geneDistribution[] has to be exactly two doubles, between 0 and 1");
			// e.g {0.33,0.33} = All genes are equally probable
		}

		Random r = new Random();
		for (int i = 0; i < genes.size(); i++) {
			// In this way the different genes are weighted so that the NONE gene is more
			// probable to be assigned. This should result in the algorithm being less
			// "picky".
			double R = r.nextDouble();
			if (R < geneDistribution[0]) {
				genes.set(i, ThreeState.HIGHER);// original candle is HIGHER than the comparison candle
			} else if (R > geneDistribution[0] && R < geneDistribution[0] + geneDistribution[1]) {
				genes.set(i, ThreeState.LOWER);

			} else {
				genes.set(i, ThreeState.NONE); // not important comparison

			}

		}
	}

	/**This method adds one trade to the list of trades*/
	public void addTrade(double trade) {
		trades.add(trade);
	}
	
	/**This method adds a whole list of trades to the list of trades*/
	public void addTrades(ArrayList<Double> tradeList) {
		for (double d : tradeList)
			trades.add(d);
	}

	/** Calculates the statistics for the trades in the list*/
	public void generateStatistics() {
		for (double d : trades) {
			accumulation *= d;
			average += d;
		}

		average /= trades.size();

		double i = 0;
		for (double d : trades) {
			if (d > 1)
				i++;
		}
		winningTrades = (double) i / (double) trades.size();

	}

	/** 0=nCandles, 1=nGenes, 2=nTrades, 3=average, 4=accumulation, 5=winningTrades */
	public double getStatistics(int index) {
		switch (index) {
		case 0:
			return nCandles;
		case 1:
			return genes.size();
		case 2:
			return (double) trades.size();
		case 3:
			return average;
		case 4:
			return accumulation;
		case 5:
			return winningTrades;
		}
		return (Double) null;
	}
	
	/**Returns the trade with the given index*/
	public double getTrade(int index) {
		return trades.get(index);
	}

	/** Returns a string with the properties of the candle*/
	public String toString() {
		String result = "";
		for (int i = 0; i < genes.size(); i++) {
			switch (genes.get(i)) {
			case HIGHER:
				result += "[HIGHER]";
				break;
			case LOWER:
				result += "[LOWER]";
				break;
			case NONE:
				result += "[NONE]";
				break;

			}
		}
		return result;
	}

	/** This method return the complete statistics of a formation */
	public String Rapport() {
		return toString() + "\n" + "AVG: " + round2Places((average - 1) * 100) + "% ACC: "
				+ round2Places((accumulation) * 100) + "%. n trades: " + trades.size() + " W/L: "
				+ round2Places((winningTrades) * 100) + "%.";
	}

	private String round2Places(double value) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		return df.format(value);
	}

	public void setGenes(ArrayList<ThreeState> newGenes) {
		if (newGenes.size() == genes.size()) {
			genes = newGenes;
		}
	}

	public ArrayList<ThreeState> getGenes() {
		return genes;
	}

	private void trade() {
		trades = new ArrayList<Double>();
		int nCandles = (int) this.getStatistics(0);
		for (Stock stock : stocks) { // All the stocks in the data-set
			for (int day = nCandles - 1; day < stock.length(); day++) { // All the candles in the stock
				boolean match = true;
				// The following is where the candles are compared
				outerloop: for (int j = 0; j < (nCandles - 1); j++) {// The original candle
					for (int k = 1; k < (nCandles); k++) { // The comparison candle

						Candlestick OC = stock.getCandle(day - j); // OC = originalCandle
						Candlestick CC = stock.getCandle(day - k); // CC = comparisonCandle

						for (int home = 0; home < 4; home++) {// candle which the comparison starts from
							for (int away = 0; away < 4; away++) {
								ThreeState ts = this.getGenes().get(away + home * 4);
								switch (ts) {
								case HIGHER:
									if (OC.getValue(home) < CC.getValue(away)) {
										match = false;
										break outerloop;
									}
									break;

								case LOWER:
									if (OC.getValue(home) > CC.getValue(away)) {
										match = false;
										break outerloop;
									}
									break;
								case NONE:
									break;
								}
							}
						}
					}
				}
				if (match) {
					this.addTrade(stock.getCandle(day).getValue(4) - cost);
				}
			}
		}
		this.generateStatistics();
	}

	public void run() {
		trade();
	}

}

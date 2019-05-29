package processing;

import data.DataManager;
import trading.Population;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		// This parts get the data and puts it in a stock array that contains the
		// candlesticks

		DataManager DM = new DataManager(5, 3, 3, 3);
		DM.addStock("HM-B.ST", true); // Training
		DM.addStock("VOLV-B.ST", true); // Training
		DM.addStock("TELIA.ST", true); // Training
		DM.addStock("SKA-B.ST", true); // Training
		DM.addStock("SKF-B.ST", true); // Training
		DM.addStock("SAND.ST", true); // Training
		DM.addStock("INVE-B.ST", true); // Training

		
		DM.addStock("ATCO-B.ST", false); // Validation
		DM.addStock("KINV-B.ST", false); // Validation
		
//---------------------------------------------------------------------------------------------
		Population population = new Population();
		int size = (int) Math.pow(100, 2); // population size
		int nGenerations = 20;
		int nCandles = 2;
		int statistic = 4;
		population.generateRandomPopulation(size, nCandles, new double[]{0.2,0.2});

		for (int n = 0; n < nGenerations; n++) { // GENERATIONS
			System.out.println("Gen: " + n + " ");
			population.trade(DM.getTrainingData(),0.005);

			population.sort(statistic);
			System.out.println(population.getFormation(0).getStatistics(statistic));

			population.breed(0, 0.3);
			//population.breed2(0, 0.3, statistic);
		}

		population.trade(DM.getValidationData(),0.005);
		population.sort(statistic);

		for (int i = 0; i < 3; i++) {
			String str = "";
			for (int j = 0; j < 6; j++) {
				str += String.valueOf(population.getFormation(i).getStatistics(j) + ",");
			}
			str += population.getFormation(0).toString();
			str += "\n";
			System.out.println(str);
		}

	}

}

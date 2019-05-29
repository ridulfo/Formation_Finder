package processing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import data.DataManager;
import trading.Population;

public class valueTesting {

	public static void main(String[] args) throws InterruptedException, IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt", false));
		writer.close();
		// Open given file in append mode.
		writer = new BufferedWriter(new FileWriter("results.txt", true));
		
		// This parts get the data and puts it in a stock array that contains the
		// candlesticks
		for (int days = 2; days < 10; days++) {
			DataManager DM = new DataManager(days, 3, 3, 3);
			DM.addStock("HM-B.ST", true); // Training
			DM.addStock("VOLV-B.ST", true); // Training
			DM.addStock("TELIA.ST", true); // Training
			DM.addStock("SKA-B.ST", true); // Training
			DM.addStock("SKF-B.ST", true); // Training
			DM.addStock("SAND.ST", true); // Training

			DM.addStock("ATCO-B.ST", false); // Validation
			DM.addStock("KINV-B.ST", false); // Validation
			// DM.addStock("INVE-B.ST", false); // Validation

//---------------------------------------------------------------------------------------------
			Population population = new Population();
			int size = (int) Math.pow(100, 2); // population size
			int nGenerations = 20;
			for (int nCandles = 2; nCandles < 5; nCandles++) {
				int statistic = 4;
				for (double prob = 0.05; prob < 0.45; prob += 0.05) {
					population.generateRandomPopulation(size, nCandles, new double[] { prob, prob });
					for (double mutationRate = 0; mutationRate < 0.5; mutationRate += 0.1) {
						for (int n = 0; n < nGenerations; n++) { // GENERATIONS
							System.out.println("Gen: " + n + " ");
							population.trade(DM.getTrainingData(), 0.005);

							population.sort(statistic);
							System.out.println(population.getFormation(0).getStatistics(statistic));

							population.breed(0, prob);
							// population.breed2(0, 0.3, statistic);
						}

						population.trade(DM.getValidationData(), 0.005);
						population.sort(statistic);

						for (int i = 0; i < 3; i++) {
							String str = "";
							for (int j = 0; j < 6; j++) {
								str += String.valueOf(population.getFormation(i).getStatistics(j) + ",");
							}
							str += population.getFormation(0).toString();
							str += "\n";
							writer.write(str);
							//System.out.println(str);
						}

					}
				}
			}
		}
	}
}

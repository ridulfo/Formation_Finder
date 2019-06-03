package trading;

import java.util.ArrayList;
import java.util.Random;

import data.Stock;
import trading.Formation.ThreeState;

public class Population {
	ArrayList<Formation> formations;
	public int size = 0;
	public int candles = 0;

	public Population() {
		formations = new ArrayList<Formation>();
	}

	/**
	 * Generates a random population. This is used when initiating the population
	 */
	public void generateRandomPopulation(int size, int candles, double[] geneDistribution) {
		this.size = size;
		this.candles = candles;

		for (int i = 0; i < size; i++) {
			formations.add(new Formation(candles, true, geneDistribution));
		}
	}

	/** Adds a single formation to the population */
	public void addFormation(Formation formation) {
		formations.add(formation);
	}

	public void sort(int statistic) {
		ArrayList<Formation> sorted = new ArrayList<Formation>();

		// The following code adds the first trader with more than a trade to the sorted
		// list. This is because there needs to be at least one in the list before we
		// start
		int i = 0;
		while (true) {
			if (!Double.isNaN(formations.get(i).getStatistics(statistic))) {
				sorted.add(formations.get(i)); // Adding an element to the sorted array.
				formations.remove(i); // Removing that element so that it does not appear twice.
				break;
			}
			i++;
		}

		// Here the formations that have more than a trade are added to the sorted list
		// so that the list is sorted when all the formations have been added.
		for (Formation form : formations) {
			if (form.getStatistics(2) > 0) {
				i = 0;
				// while the form about to be added is bigger than the form in the sorted list
				while (sorted.size() - 1 >= i
						&& form.getStatistics(statistic) < sorted.get(i).getStatistics(statistic)) {
					i++;
				}
				sorted.add(i, form);
			}
		}
		formations = sorted;
	}

	/** Returns a formation from the population */
	public Formation getFormation(int index) {
		return formations.get(index);
	}

	private ThreeState randomState() {
		Random r = new Random();
		switch (r.nextInt(3)) {
		case 0:
			return ThreeState.HIGHER;
		case 1:
			return ThreeState.LOWER;
		case 2:
			return ThreeState.NONE;
		}
		return null;
	}

	/**
	 * If the genes are the same, they are passed on. If not a random one is passed
	 * on.
	 */
	private ArrayList<ThreeState> equalMerge(Formation form1, Formation form2) {
		ArrayList<ThreeState> newGenes = new ArrayList<ThreeState>();

		for (int i = 0; i < form1.getStatistics(1); i++) {

			if (form1.getGenes().get(i).equals(form2.getGenes().get(i))) {
				newGenes.add(form1.getGenes().get(i));
			} else {
				newGenes.add(randomState());
			}

		}
		return newGenes;
	}

	/** Genes are randomly choosen between the parent formations */
	private ArrayList<ThreeState> stripMerge(Formation form1, Formation form2) {
		Random r = new Random();
		ArrayList<ThreeState> newGenes = new ArrayList<ThreeState>();

		for (int i = 0; i < form1.getStatistics(1); i++) {
			if (r.nextInt(10) == 0)
				newGenes.add(randomState());
			else {
				if (r.nextBoolean())
					newGenes.add(form1.getGenes().get(i));
				else
					newGenes.add(form2.getGenes().get(i));
			}
		}
		return newGenes;
	}

	/**
	 * At a random place in the genetic code the genes are crossed between the two parent
	 * formations
	 */
	private ArrayList<ThreeState> crossMerge(Formation form1, Formation form2) {
		Random r = new Random();
		ArrayList<ThreeState> newGenes = new ArrayList<ThreeState>();
		int switchPoint = r.nextInt(form1.getGenes().size());

		if (r.nextBoolean()) {
			// The beginning of the genes belong to Form1
			for (int i = 0; i < switchPoint; i++) {
				newGenes.add(form1.getGenes().get(i));
			}
			for (int i = switchPoint; i < form1.getGenes().size(); i++) {
				newGenes.add(form2.getGenes().get(i));
			}
		} else {
			// The beginng of the genes belong to Form2
			for (int i = 0; i < switchPoint; i++) {
				newGenes.add(form2.getGenes().get(i));
			}
			for (int i = switchPoint; i < form1.getGenes().size(); i++) {
				newGenes.add(form1.getGenes().get(i));
			}
		}
		return newGenes;
	}

	/**
	 * Breeds so that every formation in the inputarray is breeded with eachother
	 * (even itself). MergeType (0=Stripmerge, 1=equalMerge, 2 = crossMerge)
	 */
	public void breed(int mergeType, double mutationProbability) {
		ArrayList<Formation> newPop = new ArrayList<Formation>();

		for (int i = 0; i < Math.sqrt(size); i++) {
			for (int j = 0; j < Math.sqrt(size); j++) {
				ArrayList<ThreeState> genes = new ArrayList<ThreeState>();

				switch (mergeType) {
				case 0:
					genes = stripMerge(formations.get(i), formations.get(j));
					break;
				case 1:
					genes = equalMerge(formations.get(i), formations.get(j));
					break;
				case 2:
					genes = crossMerge(formations.get(i), formations.get(j));
					break;
				default:
					throw new java.lang.Error("Incorrect mergetype");
				}

				Random r = new Random();
				for (int k = 0; k < genes.size(); k++) {
					if (r.nextDouble() < mutationProbability) {
						genes.set(k, randomState());
					}
				}

				Formation f = new Formation((int) formations.get(i).getStatistics(0));
				f.setGenes(genes);
				newPop.add(f);

			}
		}
		formations = newPop;
	}

	public void breed2(int mergeType, double mutationProbability, int statistic) {
		ArrayList<Formation> newPop = new ArrayList<Formation>();
		Random r = new Random();
		double total = 0;
		for (Formation f : formations) {
			total += f.getStatistics(statistic);
		}

		while (newPop.size() < size) {
			int i = r.nextInt(formations.size());
			double fitness1 = formations.get(i).getStatistics(statistic) - 1;// / total;
			if (r.nextDouble() < fitness1) {
				int j = r.nextInt(formations.size());
				double fitness2 = formations.get(j).getStatistics(statistic) - 1;// / total;
				if (r.nextDouble() < fitness2) {
					ArrayList<ThreeState> genes = new ArrayList<ThreeState>();

					switch (mergeType) {
					case 0:
						genes = stripMerge(formations.get(i), formations.get(j));
						break;
					case 1:
						genes = equalMerge(formations.get(i), formations.get(j));
						break;
					case 2:
						genes = crossMerge(formations.get(i), formations.get(j));
						break;
					default:
						throw new java.lang.Error("Incorrect mergetype");
					}

					for (int k = 0; k < genes.size(); k++) {
						if (r.nextDouble() < mutationProbability) {
							genes.set(k, randomState());
						}
					}

					Formation f = new Formation((int) formations.get(i).getStatistics(0));
					f.setGenes(genes);
					newPop.add(f);
				}
			}
		}
		formations = newPop;
	}

	public void trade(ArrayList<Stock> stocks, double cost) throws InterruptedException {
		formations.get(0).stocks = stocks;
		formations.get(0).cost = cost;
		for (int form = 0; form < formations.size(); form++) {// All the formations in population
			Thread thread = new Thread(formations.get(form));
			thread.start();

			while (Thread.activeCount() > 100) {
			}

			if (form == formations.size() - 1) {
				thread.join();
			}
		}
	}
}

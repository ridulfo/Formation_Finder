# FormationFinder

###### The goal of this project is to find candlestick-formations that generate profits n days later.

### Preface:
At a seminar a the speaker said that he had found new patterns (formations) in the stockmarket that gave him an edge. He also explaned that the "old" formations had lost their edge due to that everybody was using them. This gave me the idea of also finding new formations. However, instead of looking throught tonnes of data and trying to find formations manually. A machine learning model could be created.

The idea is to use evolutionary algorithms to find formations that would yield a profit after n days. Henceforth, a "formation" is a description of a candlestick-formation (more on this later).

**Overview of the steps**
1) First a population of random formations would be created.
2) Every formation would be traded on historical open-high-low-close prices (OHLC).
3) The formations are sorted after a chosen statistic. For example: average profit, accumulated profit or standard deviation.
4) The top formations would be breeded together to form a new population.
5) Repeat step 2 -> 5 until satisfied with results.
6) Test the final generation on unseen data.
7) Sort

## Introduction
The following will explain the various components of program. The components are ordered in order of dependence, the most depended on component first.
### Candlesticks
A candlestick is nothing more that an objects that holds OHLC.
```Java
public Candlestick(String date, double open, double high, double low, double close) {
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
	}
```
A candle also stores the percentage difference between its price and the price n candles infront of itself. This is what a trader appends to its "trades" list when a trade has been made. The profit is added when a stock is created.

### Stock
A stock is an object that contains all the candles. This object is needed so that profits can be generated, among other functions.

### Formation
A formation can be seen as a filter. If the formation matches with the data, a trade is made. If not, nothing happens. It compares at least two candles with its genes.
#### Genes
The filtering part of a formation is its genes. The genes are what sets formations apart from each other. A gene can have one of three values. 
```Java
public enum ThreeState {HIGHER, LOWER, NONE};
```
(I decided to call the type "ThreeState" for the lack of a better name...
![Illustration of genes](GenesIllustration.png?raw=true "Illustration of genes")
The image above shows a candlestickformation of two candles. The text under the formation is the genetic description of this particular formation. The first row are the genes. The second is just an explanation of what part of the OHCL is being compared. For example, C0 means the close price of the candle with a zero on it.
#### Comparing candles
The formations genes tell the relationship between two or more candles. For example, if we are comparing today's open with yesterday' open and the formations ThreeState is "LOWER". Then for the result to be true, today's candle has to have a lower open price than yesterdays open price. "LOWER" and "HIGHER" work in the same way. However, "NONE" treats the comparison as non-important.

#### Making a trade
If all the comparisons are true a trade is made. The trader reads the profit (a double) of the candle and adds it to a list of trades.
The majority of the program's time is spent in this method. One singel formation's trade() is not that compute intensive, but do it for hundreds of thousands formations and it'll take some time.

### Population
The population objects doesn't just contain an array of formations. It takes care of the sorting, breeding and trading.
#### Sorting
First, all the formations that did not turn a profit after trading on all the training data are removed. Then, a simple insertion-sort is used for sorting. 
#### Breeding
When two formations love each other very much... There are two ways the population breeds. (The second one is not completely finished (although it does work at the moment))
##### Breeding (1):
This way of breeding takes assumes that the initial population size is a square number. After sorting. A subset of the population with the size the squareroot of the initial population is picked. Then, every formation is breeded with any other formation. This results in the new population being the size of the square of the subset, which corresponds with the initial population size.
##### Breeding 2:
This way of breeding gives every formation a fixed probability of breeding, the probability is called fitness. For the creation of each new formation the following has to occur:
1) A random formation is picked from the population. 
2) A random number between 0 and 1 is generated. If this number is lower than the fitness the formation is allowed to breed.
3) A second formation is picked from the population at random.
4) A random number between 0 and 1 is generated. If this number is lower than the fitness of the second formation the formations breed.

The second breeding method takes a longer time to breed a new population. This is due to a lot of failed breeding attempts. However, it is able to recreate a population of any size while there only has to be at least on formation in the subset.

##### Gene-mixing
There are a couple of ways the genes of the parents can be mixed. 
1) If the gene is the same, pass it on. Else, pass on a randomly generated gene.
2) Pick randomly between the parents genes. (The one I like the best)
3) Cross over the genes at a random place in the genom. (What nature does)
##### Mutation
After the new set of genes are generated. Every genes is given a random chance to mutate into a random gene.

#### Trading
The population-object takes care of the trading of the population. To make this more effective threads are used. The stockdata is given to all the formations in a static array of stock-objects. If little trainingdata is used, creating a lot of threads creates a lot of overhead. Although, creating to few threads leaves room for speedup. I would like to make the population-object sense how many threads it should use depending on how long it takes to complete trading for each formation. A little like windowsizing in TCP.

## Datamanager
As the name suggests, this object takes care of the of the data. It is able to check if the data is already stored on the computer and see if it is up to date. If not, it uses the yahoofinance API to retrieve the data. It then stores the new and up to date data on the computer. This is to limit the GETing. This object manages two data libraries, one for training and one for validating.

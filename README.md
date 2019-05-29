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
### Formation
A formation can be seen as a filter. If the formation matches with the data, a trade is made. If not, nothing happens. It compares at least two candles with its genes.

---
#### Genes
The filtering part of a formation is its genes. The genes are what sets formations apart from each other. A gene can have one of three values. 
```Java
public enum ThreeState {HIGHER, LOWER, NONE};
```
(I decided to call the type "ThreeState" for the lack of a better name...)

---

#### Comparing candles
The formations genes tell the relationship between two or more candles. 







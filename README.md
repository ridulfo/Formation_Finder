# FormationFinder

###### The goal of this project is to find candlestick-formations that generate profits n days later.

## Preface:
At a seminar a the speaker said that he had found new patterns (formations) in the stockmarket that gave him an edge. He also explaned that the "old" formations had lost their edge due to that everybody was using them. This gave me the idea of also finding new formations. However, instead of looking throught tonnes of data and trying to find formations manually. A machine learning model could be created.

The idea is to use evolutionary algorithms to find formations that would yeild a profit after n days. Henceforth, a "formation" is a description of a candlestick-formation (more on this later).
**Overview of the steps**
1) First a population of random formations would be created.
2) Every formation would be applied to historical open-high-low-close prices (OHLC).
3) The formations are sorted after a chosen statistic. For example: average profit, accumulated profit or standard deviation.
4) The top formations would be breeded together to form a new population.
5) Repeat step 2 -> 5.




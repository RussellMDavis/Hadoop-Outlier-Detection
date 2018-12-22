library(readxl)
PerfData <- read_excel("C:/Users/russb/Desktop/PerfData.xlsx")
avgData <- PerfData[c(6,12), ]
final_df <- as.data.frame(t(avgData))
final_df$index = seq.int(nrow(final_df))
names(final_df) <- c("smallDataset","largeDataset","MapReduceStep")
ggplot(final_df, aes(x = MapReduceStep)) + 
  geom_line(aes(y = smallDataset, colour = "Small Dataset"), size = 1.5) + 
  geom_line(aes(y = largeDataset, colour = "Large Dataset"), size = 1.5) + labs(y = "Time (s)") +
  scale_x_continuous(breaks = c(1, 2, 3, 4, 5, 6))

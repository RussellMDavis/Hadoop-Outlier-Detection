library(readr)
library(ggplot2)
points <- read_csv("Github/DS503/FinalProject/points.csv",
                   col_names = FALSE)
names(points) <- c("X","Y")



p <- ggplot(points, aes(x = X, y = Y)) + geom_point() + geom_point(data = top_right_point, colour = "blue")
p + theme(panel.background = element_rect(fill = "lightblue",
                                            colour = "lightblue",
                                            size = 0.5, linetype = "solid"),
            panel.grid.major = element_line(size = 0.5, linetype = 'solid',
                                            colour = "white"), 
            panel.grid.minor = element_line(size = 0.25, linetype = 'solid',
                                            colour = "white")
)

grid_for_fuzzy_boundary = subset(points, X < 250 & Y < 250)
grid_for_fuzzy_boundary$index = seq.int(nrow(grid_for_fuzzy_boundary))

small_plot <- ggplot(grid_for_fuzzy_boundary, aes(x = X, y = Y)) + geom_point() + scale_x_continuous(breaks = c(0, 125), limits = c(0,250)) + scale_y_continuous(breaks = c(0, 125, 250), limits = c(0,250))

small_plot <- small_plot + theme(panel.background = element_rect(fill = "lightblue", colour = "lightblue"), panel.grid.major = element_line(colour = "black"), panel.grid.minor = element_blank())

small_plot <- small_plot + annotate("rect", xmin = 0, xmax = 125, ymin = 0, ymax = 125, fill = "grey", alpha = 0.5)

top_right_point <- subset(points, X > 900 & Y > 600)

full_knn <- subset(points, X > 750 & Y > 300)

p <- ggplot(points, aes(x = X, y = Y)) + geom_point() + geom_point(data = full_knn, colour = "red") + geom_point(data = top_right_point, colour = "blue")
p + theme(panel.background = element_rect(fill = "lightblue",
                                          colour = "lightblue",
                                          size = 0.5, linetype = "solid"),
          panel.grid.major = element_line(size = 0.5, linetype = 'solid',
                                          colour = "white"), 
          panel.grid.minor = element_line(size = 0.25, linetype = 'solid',
                                          colour = "white")
)

set.seed(101)
sample <- sample.int(n = nrow(points), size = floor(.75*nrow(points)), replace = F)
train <- points[sample, ]
test  <- points[-sample, ]

p <- ggplot(test, aes(x = X, y = Y)) + geom_point() + geom_point(data = top_right_point, colour = "red")
p + theme(panel.background = element_rect(fill = "lightblue",
                                          colour = "lightblue",
                                          size = 0.5, linetype = "solid"),
          panel.grid.major = element_line(size = 0.5, linetype = 'solid',
                                          colour = "white"), 
          panel.grid.minor = element_line(size = 0.25, linetype = 'solid',
                                          colour = "white")
)
 
new_knn <- subset(test, X > 500 & Y > 230)
p <- ggplot(test, aes(x = X, y = Y)) + geom_point() + geom_point(data = new_knn, colour = "red") + geom_point(data = top_right_point, colour = "blue")
p + theme(panel.background = element_rect(fill = "lightblue",
                                          colour = "lightblue",
                                          size = 0.5, linetype = "solid"),
          panel.grid.major = element_line(size = 0.5, linetype = 'solid',
                                          colour = "white"), 
          panel.grid.minor = element_line(size = 0.25, linetype = 'solid',
                                          colour = "white")
)
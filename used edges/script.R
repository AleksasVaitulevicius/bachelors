#----------------calculate avearges function---------------
calculateAverages <- function(data){
  aggregate(data[, 3:12], list(data$vertices_number, data$edges_number), mean)
}

calculateAverages(usedEdges)


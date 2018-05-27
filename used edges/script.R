
usedEdgesAverage <- aggregate(
    usedEdges[, 3:12], list(usedEdges$vertices_number, usedEdges$edges_number), mean
  )
write.csv(usedEdgesAverage, "usedEdgesAverage.csv")
mod <- glm(ER ~ RT + isDots * cohFac + isLeft + blocknum,data=subj8, family=binomial(link="logit"))
package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.pagerank.cosimrank

import it.unimi.dsi.law.rank.PageRank
import it.unipi.di.acubelab.webgraph.rank.WeightedPageRankPowerSeries
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness

class WeightedCoSubSimRank (wikiGraph: WikiGraph, iterations: Int = 30,
                            pprDecay: Float = 0.8f, csrDecay: Float = 0.8f,
                            relatedness: Relatedness)

  extends CoSimRankPowerSeries(wikiGraph, iterations, pprDecay, csrDecay) {

  override def getPageRanker(): PageRank = {
    logger.info("Initializing WegihtedPageRankPowerSeries...")

    val pageRanker = new WeightedPageRankPowerSeries(wikiGraph, relatedness)
    pageRanker.alpha = pprDecay.toDouble

    pageRanker
  }

}
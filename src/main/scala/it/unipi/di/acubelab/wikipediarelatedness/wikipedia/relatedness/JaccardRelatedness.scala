package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.WikiGraph

/**
  *
  * @param options
  *                {
  *                   graph: inGraph/outGraph/symGraph
  *                }
  */
class JaccardRelatedness(options: Map[String, Any]) extends Relatedness {
  val graphName = if (options.contains("graph")) options("graph").toString else "inGraph"
  val graph = WikiGraph.wikiBVGraph(graphName)

  def computeRelatedness(wikiRelTask: WikiRelTask) : Double = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    val intersection = graph.linkIntersection(srcWikiID, dstWikiID)
    if (intersection == 0) return 0.0

    return (Similarity.cosineSimilarity(graph.successorArray(srcWikiID), graph.successorArray(srcWikiID)))

    val sizeA = graph.outdegree(srcWikiID)
    val sizeB = graph.outdegree(dstWikiID)

    intersection / (sizeA + sizeB - intersection).toDouble
  }

  override def toString () : String = { "Jaccard_%s".format(graphName) }
}

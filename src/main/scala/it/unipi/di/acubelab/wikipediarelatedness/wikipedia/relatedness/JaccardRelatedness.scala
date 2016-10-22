package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.options.JaccardOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.SetOperations
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory

/**
  *
  * @param options
  *                {
  *                   graph: inGraph/outGraph/symGraph
  *                }
  */
class JaccardRelatedness(val options: JaccardOptions) extends Relatedness {
  val wikiGraph = WikiGraphFactory.wikiBVGraph(options.graph)
  val setOperations = new SetOperations(wikiGraph)

  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val intersection = setOperations.linkIntersection(srcWikiID, dstWikiID)
    if (intersection == 0) return 0.0f

    val sizeA = wikiGraph.outdegree(srcWikiID)
    val sizeB = wikiGraph.outdegree(dstWikiID)

    intersection / (sizeA + sizeB - intersection).toFloat
  }

  override def toString () : String = { "Jaccard_%s".format(options.graph) }
}

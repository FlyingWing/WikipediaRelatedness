package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.SetOperations
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory

class BibliographicCopulingRelatedness(val options: RelatednessOptions) extends Relatedness {

  protected val graph = WikiBVGraphFactory.make("out")
  protected val operations = new SetOperations(graph)

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = operations.intersectionSize(srcWikiID, dstWikiID)

  override def toString() = "BibliographiCopuling"

}
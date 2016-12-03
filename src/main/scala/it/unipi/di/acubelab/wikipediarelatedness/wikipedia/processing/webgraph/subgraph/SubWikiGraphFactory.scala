package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph.topk.{DeepWalkSubWikiGraph, ESASubWikiGraph, W2VSubWikiGraph}


object SubWikiGraphFactory {

  def make(subGraph: String, srcWikiID: Int, dstWikiID: Int,
           wikiGraphName: String = "outGraph", threshold : Int = 1000) = {

    val wikiGraph = WikiGraphFactory.makeWikiGraph(wikiGraphName)

    subGraph match {

      case "neigh" | "neighborhood" => new NeighSubWikiGraph(srcWikiID, dstWikiID, wikiGraph)

      case "esa" => new ESASubWikiGraph(srcWikiID, dstWikiID, wikiGraph, threshold)
      case "w2v" => new W2VSubWikiGraph(srcWikiID, dstWikiID, wikiGraph, threshold)
      case "dw" => new DeepWalkSubWikiGraph(srcWikiID, dstWikiID, wikiGraph, threshold)

      case _ => throw new IllegalArgumentException("%s is not a valid subGraph generation techinque.".format(subGraph))
    }
  }

}

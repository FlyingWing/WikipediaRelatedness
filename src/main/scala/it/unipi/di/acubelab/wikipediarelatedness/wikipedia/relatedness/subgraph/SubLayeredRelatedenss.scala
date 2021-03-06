package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiLayeredJungGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topk.TopKSubNodeCreator
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions

class SubLayeredRelatedenss(options: RelatednessOptions) extends SubGraphRelatedness(options) {

  protected val layeredSubNodeCreator = subNodeCreator.asInstanceOf[TopKSubNodeCreator]


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    val srcNodes = layeredSubNodeCreator.topKNodes(srcWikiID).slice(0, options.subSize / 2)
    val dstNodes = layeredSubNodeCreator.topKNodes(dstWikiID).slice(0, options.subSize / 2)

    val subGraph = new WikiLayeredJungGraph(srcWikiID, dstWikiID, srcNodes, dstNodes, weighter)

    simRanker.similarity(srcWikiID, dstWikiID, subGraph).toFloat
  }

  override def toString() = "Layered_%s".format(subGraphString())
}
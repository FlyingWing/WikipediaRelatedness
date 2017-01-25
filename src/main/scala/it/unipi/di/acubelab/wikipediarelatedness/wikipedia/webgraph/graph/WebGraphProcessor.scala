package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph

import java.io.File

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.webgraph.{BVGraph, ImmutableGraph, Transform}
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.oldllp.{LLPProcessor, LLPTask}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.multillp.MultiLLPProcessor
import org.slf4j.LoggerFactory


class WebGraphProcessor {
  val logger = LoggerFactory.getLogger(classOf[WebGraphProcessor])

  /**
    * Builds and stores a BVGraph from the raw Wikipedia Graph.
    */
  def generateBVGraph = {
    logger.info("Building Wikipedia ImmutableGraph...")
    val outGraph = new ImmutableWikiGraph

    logger.info("Storing Wikipedia2BVGraph mapping...")
    storeMapping(outGraph.wiki2node, OldConfiguration.wikipedia("wiki2node"))

    logger.info("Storing Out Wikipedia BVGraph...")
    storeBVGraph(outGraph, OldConfiguration.wikipedia("outBVGraph"))

    logger.info("Storing In Wikipedia BVGraph...")
    storeBVGraph(Transform.transpose(outGraph), OldConfiguration.wikipedia("inBVGraph"))

    logger.info("Storing Sym Wikipedia BVGraph...")
    storeBVGraph(Transform.symmetrize(outGraph), OldConfiguration.wikipedia("symBVGraph"))

    // See http://law.di.unimi.it/software/law-docs/it/unimi/dsi/law/graph/LayeredLabelPropagation.html
    logger.info("Storing Sym and Self-loopless Wikipedia BVGraph...")
    val noLoopGraph = Transform.filterArcs(outGraph, Transform.NO_LOOPS)
    storeBVGraph(Transform.symmetrizeOffline(noLoopGraph, 20000000), OldConfiguration.wikipedia("noLoopSymBVGraph"))

    logger.info("Wikipedia has been processed as BVGraph!")
  }

  /**
    * Save graph to path as BVGraph.
    *
    * @param graph
    * @param path
    */
  def storeBVGraph(graph: ImmutableGraph, path: String) = {
    new File(path).getParentFile.mkdirs
    BVGraph.store(graph, path)
    logger.info("BVGraph stored in %s!".format(path))
  }

  def storeMapping(wiki2node: Int2IntOpenHashMap, path: String) = {
    new File(path).getParentFile.mkdirs
    BinIO.storeObject(wiki2node, path)

    val m = BinIO.loadObject(path).asInstanceOf[Int2IntOpenHashMap]
  }

  def processLLP(llpOptions: Option[Any]) : Unit = {
    logger.info("Loading Sym & loopless Wikipedia graph...")
    val graph = BVGraph.load(OldConfiguration.wikipedia("noLoopSymBVGraph"))

    llpOptions match {
      case Some(options: Map[String, Int] @unchecked) =>
        val llpTask = LLPTask.makeFromOption(options)

        new LLPProcessor(graph, llpTask).process()

      case _ => new LLPProcessor(graph).process()
    }
  }

  def processMultiLLP(llpOptions: Option[Any]) : Unit = {
    logger.info("Loading Sym & loopless Wikipedia graph...")
    val graph = BVGraph.load(OldConfiguration.wikipedia("noLoopSymBVGraph"))

    llpOptions match {
      case Some(options: Map[String, Int]@unchecked) =>
        val nLLP = options.getOrElse("nLLP", 10.0).asInstanceOf[Double].toInt
        val llpTask = LLPTask.makeFromOption(options)

        new MultiLLPProcessor(graph, nLLP, llpTask).process()

      case _ => new LLPProcessor(graph).process()
    }
  }

  /*def computeDistances(wikiSimDataset: WikiSimDataset) = {
    logger.info("Computing Wikipedia Graph distances of WikiSimDataset...")

    val symGraph = WikiGraphFactory.symGraph
    val distances = scala.collection.mutable.HashMap.empty[Tuple2[Int, Int], Int]

    var i = 1
    wikiSimDataset.foreach {
      case wikiRelTask =>
        val srcWikiID = wikiRelTask.src.wikiID
        val dstWikiID = wikiRelTask.dst.wikiID

        if (!distances.contains((srcWikiID, dstWikiID))) {
          val dist = symGraph.distance(srcWikiID, dstWikiID)
          distances.put((srcWikiID, dstWikiID), dist)
          distances.put((dstWikiID, srcWikiID), dist)
        }

        logger.info("%d distances computed.".format(i))
        i += 1
    }

    logger.info("Serializing distances...")
    val distFile = new File(Configuration.wikipedia("symDistances"))
    distFile.getParentFile.mkdirs()

    BinIO.storeObject(distances.toMap, distFile)
  }*/
}

package it.unipi.di.acubelab.graphrel.wikipedia.processing.llp

import java.io.File
import java.nio.file.Paths

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList, IntOpenHashSet}
import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.graphrel.utils.Configuration
import it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph.WikiBVGraph
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.parsing.json.JSON

class LLPClustering(llpTask: LLPTask = new LLPTask, dirPath: String = null) {
  val logger = LoggerFactory.getLogger(classOf[LLPClustering])
  val gammas = loadGammas()
  val labels = loadLabels()

  logger.info(labels.get(3966054).toString)
  logger.info(labels.get(3383).toString)
  logger.info(labels.get(15822899).toString)
  logger.info(labels.get(33183).toString)

  def loadGammas() : List[Double] = {
    val gammasPath = Paths.get(clusterDir(), "gammas.json").toString
    val gammasStr = Source.fromFile(gammasPath).getLines.mkString
    val gammas = JSON.parseFull(gammasStr)

    gammas match {
      case Some(gammaDoubles: List[Double] @unchecked) =>
        logger.info("Loaded gamma file with gammas: %s".format(gammaDoubles.toString))
        gammaDoubles

      case _ => throw new IllegalArgumentException("Can not parsing gammas.json file.")
    }
  }

  /**
    *
    * @return Map(wikiID) => [label at each iteration]?
    */
  def loadLabels() : Int2ObjectOpenHashMap[IntArrayList] = {
    logger.info("LLPLabels loading...")
    val labels =  new Int2ObjectOpenHashMap[IntArrayList]

    val diff = new IntOpenHashSet

    // Foreach files read the whole mapping at the corresponding LLP iteration.
    for (llpFile <- llpLabelsFiles()) {
      logger.info("Reading labels from %s".format(llpFile.getName))
      val it = BinIO.asIntIterator(llpFile)
      var nodeIndex = 0 // index of the Wikipedia node in the mapped BVgraph (see WikiBVGraph.wiki2node/node2wiki)

      while(it.hasNext) {
        val label = it.nextInt()
        val wikiID = WikiBVGraph.getWikiID(nodeIndex)

        labels.putIfAbsent(wikiID, new IntArrayList)
        labels.get(wikiID).add(label)
        nodeIndex += 1

        diff.add(label)
      }

      logger.info(nodeIndex.toString)
      logger.info(diff.size().toString)
    }

    labels
  }

  def clusterDir() : String = {
    val llpDir = if(dirPath == null) Configuration.wikipedia("llp") else dirPath
    Paths.get(llpDir, llpTask.toString).toString
  }

  def llpLabelsFiles() : Array[File] = {
    new File(clusterDir).listFiles.filter(_.getName.startsWith("llp_labels-")).sortBy {
      _.getName.split("-")(1).toInt
    }
  }
}

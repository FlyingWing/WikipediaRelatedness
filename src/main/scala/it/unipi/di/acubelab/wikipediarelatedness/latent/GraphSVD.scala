package it.unipi.di.acubelab.wikipediarelatedness.latent

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.WikiBVGraph
import org.slf4j.LoggerFactory

import scala.io.Source

class GraphSVD(path : String = Configuration.graphSVD("left")) {
  val logger = LoggerFactory.getLogger(classOf[GraphSVD])
  lazy val eigenVectors = loadEigenVectors(path)

  /**
    * Loads eigenvectors from path where each row is a eigenvector of ~4M of doubles.
    * @return {i-th_eigenVector: eigenVector} (0-th is the highest, 1-th the second highest, ...)
    */
  def loadEigenVectors(path: String) : ObjectArrayList[DoubleArrayList] = {
    // Each row of path is an eigenvector.
    val eigenVectors = new ObjectArrayList[DoubleArrayList]

    val reader = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )

    logger.info("Loading eigenvectors from %s...".format(path))
    for ((line, index) <- reader.getLines().zipWithIndex) {
      val values = line.split("\t").map(_.toDouble)
      eigenVectors.add(new DoubleArrayList(values))
    }

    eigenVectors
  }

  /**
    * @return Vector made by all wikiID-th compontents of the first embeddingSize eigenvectors.
    */
  def eigenEmbeddingVector(wikiID: Int, embeddingSize: Int = 0) : DoubleArrayList = {
    val wikiIndex = WikiBVGraph.getNodeID(wikiID)
    val eigenVector =  eigenVectors.get(wikiIndex)

    val size = if(embeddingSize <= 0) eigenVector.size else embeddingSize

    new DoubleArrayList(eigenVector.toDoubleArray, 0, size)
  }
}

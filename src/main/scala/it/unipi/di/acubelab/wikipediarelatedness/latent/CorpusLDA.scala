package it.unipi.di.acubelab.wikipediarelatedness.latent

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import org.slf4j.LoggerFactory

import scala.io.Source

class CorpusLDA (path : String = Configuration.lda("wiki_lda")) {
  val logger = LoggerFactory.getLogger(classOf[CorpusLDA])
  lazy val lda = loadWikiLDAEmbedding(path)

  def loadWikiLDAEmbedding(path: String):
  Int2ObjectArrayMap[ObjectArrayList[Tuple2[Int, Double]]] = {
    // {wikiID -> [(index, value)}
    val lda = new Int2ObjectArrayMap[ObjectArrayList[Tuple2[Int, Double]]]

    val reader = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )

    logger.info("Loading LDA embedding from %s...".format(path))
    for (line <- reader.getLines()) {
      val row = line.split("\t")

      val wikiID = row(0).asInstanceOf[Int]
      lda.putIfAbsent(wikiID, new ObjectArrayList[(Int, Double)]())

      row.slice(1, row.size).foreach {
        case indexValue =>
          val index = indexValue.split(",").asInstanceOf[Int]
          val value = indexValue.split(",").asInstanceOf[Double]

          lda.get(wikiID).add((index, value))
      }
    }

    lda
  }

}

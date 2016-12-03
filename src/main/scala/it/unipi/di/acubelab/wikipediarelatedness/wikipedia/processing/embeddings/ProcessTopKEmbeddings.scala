package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings

import java.io.File
import java.util.concurrent.TimeUnit

import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.{Object2ObjectOpenHashMap, ObjectArrayList}
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import org.slf4j.LoggerFactory

/**
  * Pre-process wikiID present in wikiRelTasks by saving the most top-k similar entities
  * (k = 10000) into path, parameter specified by process method.
  *
  * @param wikiRelTasks
  */
class ProcessTopKEmbeddings(wikiRelTasks: List[WikiRelateTask]) {
  val wordEntities = (wikiRelTasks.map(task => "ent_%d".format(task.src.wikiID)) ++
                      wikiRelTasks.map(task => "ent_%d".format(task.dst.wikiID)) ).distinct

  val logger = LoggerFactory.getLogger(classOf[ProcessTopKEmbeddings])


  def generate(path: String, embeddings: EmbeddingsDataset) = {
    val entity2entities = new Object2ObjectOpenHashMap[Int, ObjectArrayList[Int]]()

    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("TopKSimilar Embedding Entitites Processing...")

    var minEnts = Integer.MAX_VALUE
    wordEntities.foreach {
      case wordEntity =>

        val entities = getTopKEntities(wordEntity, embeddings)
        minEnts = Math.min(entities.size(), minEnts)

        val wikiID = wordEntity.substring(4, wordEntity.length).toInt
        entity2entities.put(wikiID, entities)

        pl.update()
    }
    logger.info("Minimum found: %d".format(minEnts))
    pl.done()

    logger.info("Serializing into file %s...".format(path))
    new File(path).getParentFile.mkdirs

    BinIO.storeObject(entity2entities, path)

    logger.info("TopKSimilar Processing end.")
  }


  /**
    * Returns WikiIDs of entities most smilar to wordEntity. Default threshold is 10000.
    *
    * @param wordEntity
    * @param embeddings
    * @return
    */
  def getTopKEntities(wordEntity: String, embeddings: EmbeddingsDataset) = {
    val topKEntities = new ObjectArrayList[Int]

    val it = embeddings.topKSimilar(wordEntity).iterator()
    while(it.hasNext) {
      val s = it.next()
      try {
        if (s.startsWith("ent_")) topKEntities.add(s.substring(4, s.length).toInt)
      } catch {
        case e: Exception => logger.info("Error with %s: %s".format(s.toString, e.toString))
      }
    }

    topKEntities
  }

}

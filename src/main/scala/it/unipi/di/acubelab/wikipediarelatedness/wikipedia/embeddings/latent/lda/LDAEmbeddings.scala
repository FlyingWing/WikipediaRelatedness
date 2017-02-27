package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent.lda

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent.LatentWikiEmbeddings
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j


class LDAEmbeddings(topics: Int) extends LatentWikiEmbeddings {
  protected val matrix = Nd4j.zeros(4730474, topics)
  protected val wiki2row = new Int2IntOpenHashMap


  /**
    * Inserts rowArray into matrix by keepin track of wikiID -> rowIndex mapping.
    *
    * @param wikiID
    * @param rowArray
    * @return
    */
  override def putRow(wikiID: Int, rowArray: Array[Float]) = {
    val rowIndex = wiki2row.size()
    wiki2row.put(wikiID, rowIndex)

    matrix.putRow( rowIndex, Nd4j.create(rowArray) )
  }


  override def apply(wikiID: Int) : INDArray = super.apply(wiki2row.get(wikiID))
}
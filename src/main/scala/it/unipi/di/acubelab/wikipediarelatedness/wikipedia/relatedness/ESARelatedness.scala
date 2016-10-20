package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.options.ESAOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.lucene.LuceneIndex

/*class ESARelatedness(options: ESAOptions)  extends  Relatedness {
  val lucene = new LuceneIndex
  val conceptThreshold = options.threshold

  override def computeRelatedness(wikiRelTask: WikiRelateTask) : Double = {

    if(wikiRelTask.src.wikiID == wikiRelTask.dst.wikiID) return 1.0

    println(wikiRelTask.wikiIDs().toString())

    val srcBody = lucene.wikipediaBody(wikiRelTask.src.wikiID)
    val dstBody = lucene.wikipediaBody(wikiRelTask.dst.wikiID)

    relatedness(srcBody, dstBody)

    //relatedness("ent_%d".format(wikiRelTask.src.wikiID),
    //  "ent_%d".format(wikiRelTask.dst.wikiID))
  }

  def relatedness(srcWord: String, dstWord: String, threshold: Int = conceptThreshold) : Double = {
    val concepts = List(srcWord, dstWord).par.map(lucene.wikipediaConcepts(_, threshold))

    val srcSCs = concepts(0)
    val dstSCs = concepts(1)

    val cosine = Similarity.cosineSimilarity(srcSCs, dstSCs)

    cosine
  }

  override def toString() : String = { "ESA_%s".format(conceptThreshold) }
}*/

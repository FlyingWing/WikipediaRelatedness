package it.unipi.di.acubelab.graphrel.wikipedia.relatedness
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.wikipedia.processing.llp.{LLPClustering, LLPTask}
import org.slf4j.LoggerFactory

class LLPRelatedness(options: Map[String, Any]) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[LLPRelatedness])

  val llpTask = LLPTask.makeFromOption(options)

  val llpLabels = new LLPClustering(llpTask)

  override def computeRelatedness(wikiRelTask: WikiRelTask): Double = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    val srcVec = llpLabels.labels.get(srcWikiID)
    val dstVec = llpLabels.labels.get(dstWikiID)

    val sim = hammingSimilarity(srcVec, dstVec)

    sim / srcVec.size.toDouble
  }

  def hammingSimilarity(src: IntArrayList, dst: IntArrayList) : Int = {
    var sim = 0
    for(i <- 0 until src.size) {
      sim += (if(src.get(i) == dst.get(i)) 1 else 0)
    }
    sim
  }

  override def toString(): String = {
    "LLP-%s".format(llpTask.toString)
  }
}

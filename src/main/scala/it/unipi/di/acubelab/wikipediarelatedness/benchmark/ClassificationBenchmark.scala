package it.unipi.di.acubelab.wikipediarelatedness.benchmark


import it.unipi.di.acubelab.wikipediarelatedness.dataset.RelatednessDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiClassTask
import org.slf4j.LoggerFactory


class ClassificationBenchmark(dataset: RelatednessDataset, relatedness: Relatedness)
  extends RelatednessBenchmark(dataset, relatedness) {
  override val logger = LoggerFactory.getLogger(classOf[ClassificationBenchmark])


  override def runBenchmark() : Unit = {
    super.runBenchmark()
    runClassificationBenchmark()
  }


  def runClassificationBenchmark() = {
    val classTasks = dataset.map(new WikiClassTask(_)).filter(_.groundClass >= 0)
  }
}



/*
*
class RelatednessBenchmark(val dataset: RelatednessDataset, val relatedness: Relatedness) {
  val logger = LoggerFactory.getLogger(classOf[RelatednessBenchmark])
  val relatednessDirectory = Paths.get(Configuration.benchmark, relatedness.toString).toString

  /**
    * Computes relatedness measure on dataset.
    * Write pairs and relatedness to a CSV file under benchmark/.
    */
  def runBenchmark() : Unit = {
    runRelatedness()
* */
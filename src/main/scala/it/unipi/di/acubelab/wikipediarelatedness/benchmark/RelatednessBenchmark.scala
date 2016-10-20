package it.unipi.di.acubelab.wikipediarelatedness.benchmark

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import com.github.tototoshi.csv.CSVWriter
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{RelatednessDataset, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.Correlation
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


class RelatednessBenchmark(val dataset: RelatednessDataset, val relatedness: Relatedness) {
  val logger = LoggerFactory.getLogger(classOf[RelatednessBenchmark])
  val relatednessDirectory = Paths.get(Configuration.benchmark, relatedness.toString).toString

  /**
    * Computes relatedness measure on dataset.
    * Write pairs and relatedness to a CSV file under benchmark/.
    */
  def runBenchmark() : Unit = {
    runRelatedness()

    writeRelatednessScores()
    writeCorrelationScores()
  }

  /**
   * Computes machineRelatedness scores for each task in dataset.
   */
  def runRelatedness() : Unit = {
    logger.info("Running Relatedness %s on dataset %s...".format(relatedness.toString, dataset))

    // Computes relatedness for each pair.
    dataset.foreach {
      case task: WikiRelateTask =>
        task.machineRelatedness = relatedness.computeRelatedness(task)
    }
  }

  /**
    * Writes scores to scoresPath CSV file.
    */
  def writeRelatednessScores() : Unit = {
    logger.info("Writing %s Relatedness scores...".format(relatedness.toString))

    new File(relatednessDirectory).mkdirs
    val path = Paths.get(relatednessDirectory, relatedness.toString() + ".data.csv").toString

    val writer = new PrintWriter(new File(path))
    dataset.foreach(task => writer.write(task.toString() + "\n"))
    writer.close()
  }

  def writeCorrelationScores() : Unit = {
    val pearson = Correlation.pearson(dataset)
    val spearman = Correlation.spearman(dataset)

    logger.info("%s Pearson: %.2f".format(relatedness.toString, pearson))
    logger.info("%s Spearman: %.2f".format(relatedness.toString, spearman))

    val path = Paths.get(relatednessDirectory, relatedness.toString + ".correlation.csv").toString
    new PrintWriter(path).write(
      "Pearson:%1.2f, Spearman: %1.2f".format(pearson, spearman)
    )
  }
}

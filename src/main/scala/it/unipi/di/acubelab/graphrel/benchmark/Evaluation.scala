package it.unipi.di.acubelab.graphrel.benchmark

import java.io.{File, PrintWriter}
import java.util.Locale

import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import org.apache.commons.math.stat.correlation.{PearsonsCorrelation, SpearmansCorrelation}

object Evaluation {

  def pearsonCorrelation(scores: List[(WikiRelTask, Double)]) : Double = {
    val (humanScores, relatedScores) = scoresToArrays(scores)
    val pearson = new PearsonsCorrelation()

    pearson.correlation(humanScores, relatedScores)
  }

  def spearmanCorrelation(scores: List[(WikiRelTask, Double)]) : Double = {
    val (humanScores, relatedScores) = scoresToArrays(scores)
    val spearman = new SpearmansCorrelation()

    spearman.correlation(humanScores, relatedScores)
  }

  def scoresToArrays(scores: List[(WikiRelTask, Double)]) : (Array[Double], Array[Double]) = {
    val humanScores = scores.map(_._1.rel).toArray
    val relatedScores = scores.map(_._2).toArray

    (humanScores, relatedScores)
  }

  def writeTmpScores(scores: Array[Double], path: String = "tmp/rel.txt") : Unit = {
    val pw = new PrintWriter(path)
    pw.write(scores.mkString(","))
    pw.close
  }
}

package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.classification.classifiers

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness

class ThresholdClassifier(rel: Relatedness, threshold: Double) extends RelatednessClassifier {
  override val relatedness = rel

  override def train(trainingData: List[WikiRelateTask], tuningData: List[WikiRelateTask]) = {}

  override def predict(wikiRelTask: WikiRelateTask): WikiRelateTask = {
    val relScore = relatedness.computeRelatedness(wikiRelTask)

    if(relScore >= threshold) wikiRelTask.make(relScore, 1) else wikiRelTask.make(relScore, 0)
  }
}

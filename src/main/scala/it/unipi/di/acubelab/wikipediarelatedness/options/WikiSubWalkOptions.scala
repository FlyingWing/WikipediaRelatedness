package it.unipi.di.acubelab.wikipediarelatedness.options

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory

class WikiSubWalkOptions(json: Option[Any] = None) extends WikiWalkOptions(json) {

  val weighting = RelatednessFactory.make(getOptionAny("weighting"))

  val subGraph = getString("subGraph", "neighborhood")


  override def toString() : String = {
    "%s,weighting:%s,subGraph:%s".formatLocal(java.util.Locale.US,
      super.toString(), weighting, subGraph
    )
  }
}

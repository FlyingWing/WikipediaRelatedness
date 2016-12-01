package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.options._
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa.{ESARelatedness, IBMESARelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.latent.{GraphSVDRelatedness, LDARelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural.{LINERelatedness, Word2VecRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.subgraph.{SubCoSimRankRelatedness, SubPPRCosRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.{CoSimRankRelatedness, PPRCosRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set.{JaccardRelatedness, JaccardTopRelatedness, LocalClusteringRelatedness, MilneWittenRelatedness}


object RelatednessFactory {
  /**
    * @param json Json string:
    *                      {
    *                          // Relatedness measure
    *                         "relatedness": MilneWitten/Jaccard/LLP/MultiLLP/w2v
    *
    *                         // Wikipedia graph to be used
    *                         "graph": "in", "out", "sym", "noloopsym"
    *                      }
    * @return Relatedness object, instatiated with the specified parameters.
    */
  def make(json: Option[Any]) : Relatedness = {
    val relatednessName = getRelatednessName(json)

    relatednessName match {
      // Set
      case "MilneWitten" => new MilneWittenRelatedness(new MilneWittenOptions(json))
      case "Jaccard" => new JaccardRelatedness(new JaccardOptions(json))
      case "LC" | "localClustering" => new LocalClusteringRelatedness(new LocalClusteringOptions(json))
      case "JaccardTop" => new JaccardTopRelatedness(new JaccardTopOptions(json))
      // case "LocalClustering" => new LocalClusteringRelatedness(new LocalClusteringOptions(json))

      // Embeddings
      case "w2v" => new Word2VecRelatedness(new Word2VecOptions(json))
      case "LINE" => new LINERelatedness(new LINEOptions(json))

      // ESA
      case "IBMESA" => new IBMESARelatedness(new IBMESAOptions(json))
      case "ESA" => new ESARelatedness(new ESAOptions(json))

      //

      /*case "LLP" => new LLPRelatedness(json)
      case "MultiLLP" => new MultiLLPRelatedness(json)
      */

      //case "LMModel" => new LMRelatedness(new LMOptions(json))

      // PageRank
      case "CoSimRank" => new CoSimRankRelatedness(new CoSimRankOptions(json))

      case "PPRCos" => new PPRCosRelatedness(new PPRCosOptions(json))


      case "SubCoSimRank" => new SubCoSimRankRelatedness(new SubCoSimRankOptions(json))
      case "SubPPRCos" => new SubPPRCosRelatedness(new SubPPRCosOptions(json))


      // Latent
      case "SVD" => new GraphSVDRelatedness(new GraphSVDOptions(json))
      case "LDA" => new LDARelatedness(new LDAOptions(json))

      case _ => throw new IllegalArgumentException("The specified relatedness does not exist %s.".format(relatednessName))
    }
  }


  def getRelatednessName(json: Option[Any]) : String = {
    json match {
      case Some(options: Map[String, Any] @unchecked) =>
        try {
          options("relatedness").toString
        } catch {
          case e: Exception => throw new IllegalArgumentException("Error in getting relatedness name")
        }
      case _ => throw new IllegalArgumentException("Error while matchin json string.")
    }
  }


  def make(relatednessName: String)  : Relatedness = {
    val json = Some(Map("relatedness" -> relatednessName))
    this.make(json)
  }

}
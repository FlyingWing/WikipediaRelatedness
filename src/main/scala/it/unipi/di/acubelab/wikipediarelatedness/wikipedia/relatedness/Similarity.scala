package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import java.util.Collections

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.objects.ObjectArrayList


object Similarity {

  def computeSimilarity(similarityName: String, src: IntArrayList, dst:IntArrayList) : Double = {
    similarityName match {
      case "hamming" => hammingSimilarity(src, dst)
      case "levenshtein" => levenshteinSimilarity(src, dst)
      case "cosine" => cosineSimilarity(src, dst)
      case  _ => throw new IllegalArgumentException("%s similarity does not exist!".format(similarityName))
    }
  }

  def cosineSimilarity(src: Array[Int], dst: Array[Int]) : Double = {
    cosineSimilarity(new IntArrayList(src), new IntArrayList(dst))
  }

  def cosineSimilarity(src: IntArrayList, dst: IntArrayList) : Double = {
    val num = hammingSimilarity(src, dst)
    val den = src.size

    num / den.toDouble
  }

  // Code from https://rosettacode.org/wiki/Levenshtein_distance#Scala
  def levenshteinSimilarity(src: IntArrayList, dst: IntArrayList) = {
    val dist = Array.tabulate(src.size + 1, dst.size + 1){(j, i) => if (j == 0) i else if (i == 0) j else 0}

    for(j <- 1 to dst.size; i <- 1 to src.size)
      dist(j)(i) = if(dst.getInt(j - 1) == src.getInt(i - 1)) dist(j - 1)(i - 1)
      else math.min(dist(j - 1)(i) + 1, math.min(dist(j)(i - 1) + 1, dist(j - 1)(i - 1) + 1))

    src.size - dist(dst.size)(src.size)
  }

  def hammingSimilarity(src: IntArrayList, dst: IntArrayList) : Int = {
    var sim = 0
    for(i <- 0 until src.size) {
      sim += (if(src.getInt(i) == dst.getInt(i)) 1 else 0)
    }

    sim
  }

  def cosineSimilarity(srcVec: DoubleArrayList, dstVec: DoubleArrayList) : Double = {
    if (srcVec.size != dstVec.size)
      throw new IllegalArgumentException("Cosine Similarity error: arrays with different sizes.")

    var dot = 0.0
    var srcMagnitude = 0.0
    var dstMagnitude = 0.0

    for(i <- 0 until srcVec.size()) {
      dot += srcVec.getDouble(i) * dstVec.getDouble(i)

      srcMagnitude += math.pow(srcVec.getDouble(i), 2.0)
      dstMagnitude += math.pow(dstVec.getDouble(i), 2.0)
    }

    if(srcMagnitude == 0.0 || dstMagnitude == 0.0) return 0.0
    val magnitude = math.sqrt(srcMagnitude) * math.sqrt(dstMagnitude)

    dot / magnitude
  }

  def cosineSimilarity(srcVec: ObjectArrayList[Tuple2[Int, Double]],
                       dstVec: ObjectArrayList[Tuple2[Int, Double]]) : Double = {
    var dot = 0.0
    val srcMagnitude = indexedVectorMagnitude(srcVec)
    val dstMagnitude = indexedVectorMagnitude(dstVec)

    var i = 0
    var j = 0
    while(i < srcVec.size() && j < dstVec.size()) {
      val srcIndex = srcVec.get(i)._1
      val dstIndex = dstVec.get(j)._1

      val srcValue = srcVec.get(i)._2
      val dstValue = dstVec.get(j)._2

      if(srcIndex == dstIndex) {
        dot += srcValue * dstValue
        i += 1
        j += 1
      } else if(srcIndex < dstIndex) i += 1 else j += 1
    }

    if (dot == 0.0) return 0.0

    if(srcMagnitude == 0.0 || dstMagnitude == 0.0) return 0.0
    val magnitude = math.sqrt(srcMagnitude) * math.sqrt(dstMagnitude)

    dot / magnitude   // avoid representation errors
  }

  def indexedVectorMagnitude(vec: ObjectArrayList[Tuple2[Int, Double]]) : Double = {
    var magnitude = 0.0

    for(i <- 0 until vec.size()) {
      magnitude += math.pow(vec.get(i)._2, 2.0)
    }

    magnitude
  }

  def cosineSimilarity(srcVec: List[Tuple2[Int, Float]], dstVec: List[Tuple2[Int, Float]]) : Double = {
    val fastSrcVec = new ObjectArrayList[Tuple2[Int, Double]]()
    val fastDstVec = new ObjectArrayList[Tuple2[Int, Double]]()

    srcVec.foreach(indexValue => fastSrcVec.add((indexValue._1, indexValue._2.toDouble)))
    dstVec.foreach(indexValue => fastDstVec.add((indexValue._1, indexValue._2.toDouble)))

    cosineSimilarity(fastSrcVec, fastDstVec)
  }

  def zeroKLSimilarity(p: List[Double], q: List[Double]) : Double = {
    val gamma = 20.0  // http://dl.acm.org/citation.cfm?id=2661887

    val P = p.map(pi => if(pi >= 0.0) pi else 0.0)
    val Q = q.map(qi => if(qi >= 0.0) qi else 0.0)

    val zeroKLDivergence = (P zip Q).foldLeft(0.0) {
      case (kl: Double, (pi: Double, qi: Double)) =>
        if (qi == 0.0 || pi == 0.0) kl + gamma
        else {
          kl + pi * math.log(pi / qi)
        }
    }

    if (zeroKLDivergence == 0.0) return 0.0

    1 / zeroKLDivergence
  }

  def testCosine() = {
    val src = List(13f, 0f, 14f, 0f, 15f).zipWithIndex.map(pair => Tuple2(pair._2, pair._1)).filter(p => p._2 != 0f)
    val dst = List(15f, 0f, 16f, 17f, 0f, 20f).zipWithIndex.map(pair => Tuple2(pair._2, pair._1)).filter(p => p._2 != 0f)

    println("%1.3f".format(cosineSimilarity(src, dst)))
  }
}

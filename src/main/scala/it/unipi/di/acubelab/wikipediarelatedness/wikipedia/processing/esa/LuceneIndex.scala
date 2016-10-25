package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa

import java.nio.file.Paths

import it.unipi.di.acubelab.wikipediarelatedness.utils.{Configuration, CoreNLP}
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.core.KeywordAnalyzer
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.util.CharArraySet
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{BooleanQuery, IndexSearcher}
import org.apache.lucene.search.similarities.BM25Similarity
import org.apache.lucene.store.{FSDirectory, IOContext, RAMDirectory}
import org.slf4j.LoggerFactory


class LuceneIndex {
  val logger = getLogger()

  val reader = loadIndexInMemory()

  val searcher = new IndexSearcher(reader)
  searcher.setSimilarity(new BM25Similarity())

  BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE)


  def getLogger() = LoggerFactory.getLogger(classOf[LuceneIndex])


  def loadIndexInMemory(): DirectoryReader = {
    logger.info("Loading Lucene index in memory...")

    val fsDir = FSDirectory.open(Paths.get(Configuration.wikipedia("lucene")))
    val directory = new RAMDirectory(fsDir, IOContext.DEFAULT)

    DirectoryReader.open(directory)
  }


  /**
    * @return List of Wikipedia IDs where wikiID is mentioned and the corresponding score.
    */
  def wikipediaConcepts(text: String, resultThreshold: Int = 625): List[Tuple2[Int, Float]] = {
    val parser = new QueryParser("body", LuceneIndex.analyzer)
    val coreText = CoreNLP.lemmatize(text).mkString(" ")

    val query = parser.createBooleanQuery("body", coreText)

    val threshold = if (resultThreshold >= 0) resultThreshold else Integer.MAX_VALUE

    val x = searcher.search(query, threshold).scoreDocs.map { hit =>
      val wikiDocID = reader.document(hit.doc).getField("id").stringValue().toInt
      (wikiDocID, hit.score)
    }.toList

    x.sortBy(_._1)
  }


  def wikipediaBody(wikiID: Int) : String = {
    val parser = new QueryParser("id", LuceneIndex.analyzer)
    val query = parser.createBooleanQuery("id", wikiID.toString)

    val wikiDoc = searcher.search(query, 1).scoreDocs(0).doc
    reader.document(wikiDoc).getField("body").stringValue()
  }


  def vectorSpaceProjection(wikiID: Int) : String = {
    val text = wikipediaBody(wikiID)
    reader.getTermVector(wikiID, "id")

    ""
  }
}

object LuceneIndex {
  lazy val analyzer = luceneEntityAnalyzer()

  def luceneEntityAnalyzer() : Analyzer = {
    val emptyStopWords = new CharArraySet(0, true)
    val bodyAnalyzer = new WikipediaBodyAnalyzer()

    val analyzerMap = new java.util.HashMap[String, Analyzer]
    //analyzerMap.put("body", bodyAnalyzer)

    analyzerMap.put("body", new StandardAnalyzer())

    return new PerFieldAnalyzerWrapper(new KeywordAnalyzer(),  analyzerMap )

    new PerFieldAnalyzerWrapper(new KeywordAnalyzer(), analyzerMap)
  }
}
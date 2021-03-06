package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa

import java.io.Reader

import it.unipi.di.acubelab.wikipediarelatedness.utils.StopWords
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents
import org.apache.lucene.analysis.core.{LowerCaseFilter, StopFilter, WhitespaceTokenizer}
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.util.CharArraySet
import org.slf4j.LoggerFactory


/**
  * Analyzer which takes into account a body where Wikipedia entities are
  * marked as ent_wikiID.
  */
class WikipediaBodyAnalyzer extends Analyzer {
  val logger = LoggerFactory.getLogger(classOf[WikipediaBodyAnalyzer])

  override def createComponents(fieldName : String) : TokenStreamComponents = {
    val whiteTokenizer = new WhitespaceTokenizer()
    val lowerFilter = new LowerCaseFilter(whiteTokenizer)
    val stopFilter = new StopFilter(lowerFilter,  WikipediaBodyAnalyzer.GOOGLE_STOP_WORDS_SET)

    new TokenStreamComponents(whiteTokenizer, stopFilter) {
      override def setReader(reader: Reader): Unit = {
        super.setReader(reader)
      }
    }
  }
}

object WikipediaBodyAnalyzer {
  /*val _googleStopWords =
    ("a about above after again against all am an and any are aren't as at be because been before being below " +
    "between both but by can't cannot could couldn't did didn't do does doesn't doing don't down during " +
    "each few for from further had hadn't has hasn't have haven't having he he'd he'll he's her here here's " +
    "hers herself him himself his how how's i i'd i'll i'm i've if in into is isn't it it's its itself let's " +
    "me more most mustn't my myself no nor not of off on once only or other ought our ours ourselves out " +
    "over own same shan't she she'd she'll she's should shouldn't so some such than that that's the their " +
    "theirs them themselves then there there's these they they'd they'll they're they've this those through " +
    "to too under until up very was wasn't we we'd we'll we're we've were weren't what what's when when's " +
    "where where's which while who who's whom why why's with won't would wouldn't you you'd you'll you're " +
    "you've your yours yourself yourselves").split(" ").toList*/

  import scala.collection.JavaConversions._
  val GOOGLE_STOP_WORDS_SET = CharArraySet.unmodifiableSet(new CharArraySet(StopWords.getStopWordList, false))
}
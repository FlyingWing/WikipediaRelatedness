package it.unipi.di.acubelab.wikipediarelatedness.server.service

import java.util.Locale

import com.twitter.finagle.http.Method._
import com.twitter.finagle.http.Version.{apply => _, _}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import it.unipi.di.acubelab.wikipediarelatedness.server.utils.ResponseParsing
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness.FastMWDWRelatedness
import org.slf4j.LoggerFactory


class LambdaWikiRelateService(lambdaRelatedness: FastMWDWRelatedness, port: Int = 9090)
  extends WikiRelateService(lambdaRelatedness, port) {

  override protected val logger = LoggerFactory.getLogger(getClass)

  override def apply(request: Request): Future[Response] = {
    try {

      request.method match {
        case Post =>
          //logger.info("Parsing request %s" format request.getContentString())

          val wikiRelateTask = ResponseParsing(request)
          val lambda = ResponseParsing.getFloat(request, "lambda")

          val queryString = "%s (%d) and %s (%d) with lambda %1.2f" format(wikiRelateTask.src.wikiTitle, wikiRelateTask.src.wikiID,
            wikiRelateTask.dst.wikiTitle, wikiRelateTask.dst.wikiID, lambda)


          wikiRelateTask.machineRelatedness = relatedness.asInstanceOf[FastMWDWRelatedness].computeRelatedness(wikiRelateTask, lambda)
          //logger.info("[%s] Computing relatedness between %s... " format (relatedness.toString, queryString) )
          logger.info("Relatedness between %s is %1.3f" formatLocal(Locale.US, queryString, wikiRelateTask.machineRelatedness))

          val response = ResponseParsing(wikiRelateTask)
          Future.apply(response)


        case _ =>
          Future.value(Response.apply(Http11, Status.NotFound))
      }

    } catch {
      // errors & co.
      case e: Exception => throw e
    }
  }
}

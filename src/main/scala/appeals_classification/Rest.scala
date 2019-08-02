package appeals_classification

import akka.http.scaladsl.model.Multipart.BodyPart
import akka.http.scaladsl.model.{Multipart, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import scala.concurrent.duration._

trait Rest {
  def predictionRoutes: Route = {
    get {
      path("name") {
        complete(StatusCodes.OK, "Project name classification-appeals-solution")
      }
    } ~
      post {
        path("text") {
          withRequestTimeout(60.seconds) {
            extractRequestContext { ext =>
              implicit val materializer = ext.materializer
              implicit val ec = ext.executionContext

              entity(as[Multipart.FormData]) { formData =>
                val extractedData: Future[Map[String, Any]] = formData.parts.mapAsync[(String, Any)](1) {
                  case p: BodyPart =>
                    p.toStrict(2.seconds).map(strict => p.name -> strict.entity.data.utf8String)
                }.runFold(Map.empty[String, Any])((map, tuple) => map + tuple)

                onSuccess((extractedData)) { data =>
                  val text = data("text").toString
                  val response = RequestHandler.handleInputText(text)
                  val json = RequestHandler.toJson(response)
                  complete(s"$json")
                }
              }
            }
          }
        }
      }
  }

  val routes = predictionRoutes

}

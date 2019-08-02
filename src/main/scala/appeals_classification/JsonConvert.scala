package appeals_classification

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object JsonConvert extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val predictFormat = jsonFormat3(Prediction.apply)
}


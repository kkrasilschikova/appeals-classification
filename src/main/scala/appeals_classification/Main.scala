package appeals_classification

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.concurrent.duration._

object Main extends Rest with LazyLogging {
  /*val spark = SparkSession
    .builder()
    .appName("classification-appeals")
    .master("local")
    .getOrCreate()

  import spark.implicits._*/

  def main(args: Array[String]): Unit = {
    val host = "0.0.0.0"
    val port = 8080

    implicit val system = ActorSystem("classification-appeals")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    implicit val timeout = Timeout(120 seconds)

    val api = routes

    Http().bindAndHandle(handler = api, interface = host, port = port) map { binding =>
      logger.info(s"REST interface bound to ${binding.localAddress}")
    } recover { case ex =>
      logger.error(s"REST interface could not bind to $host:$port", ex.getMessage)
    }

    /*
    // load json file
    val json: DataFrame = spark.read
      .option("multiline", "true")
      .json(".\\src\\main\\resources\\Ryazan_NashDomRyazan-29-03-2019.json") // Windows path
      .na.drop()
      .filter(length($"text") > 0)
      json.printSchema()

    // create models to predict Category, Theme and Executor
    Model.createModel(json, "Category")
    Model.createModel(json, "Theme")
    Model.createModel(json, "Executor")

    // check on new data
    handleInputText("около дома не убирают мусор")
    // results
    // Category - Дворовая территория
    // Theme - Неубранная дворовая территория (мусор/снег)
    // Executor - Управление благоустройства города


    spark.stop()*/
  }

}
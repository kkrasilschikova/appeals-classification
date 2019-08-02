package appeals_classification

import appeals_classification.JsonConvert._
import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.conf.Configuration
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.SparkSession
import spray.json._

import scala.util.Random

case class Prediction(category: String, theme: String, executor: String)

object RequestHandler extends LazyLogging {
  val spark = SparkSession
    .builder()
    .appName("classification-appeals")
    .master("local")
    .getOrCreate()

  // to get rid of java.io.IOException: No FileSystem for scheme: file
  val hadoopConfig: Configuration = spark.sparkContext.hadoopConfiguration
  hadoopConfig.set("fs.hdfs.impl", classOf[org.apache.hadoop.hdfs.DistributedFileSystem].getName)
  hadoopConfig.set("fs.file.impl", classOf[org.apache.hadoop.fs.LocalFileSystem].getName)

  val modelCategory = PipelineModel.load(s".\\src\\main\\resources\\modelCategory")
  val modelTheme = PipelineModel.load(s".\\src\\main\\resources\\modelTheme")
  val modelExecutor = PipelineModel.load(s".\\src\\main\\resources\\modelExecutor")

  def handleInputText(text: String): Prediction = {
    val inputData = spark.createDataFrame(Seq((Random.nextInt(), text)))
      .toDF("id", "text")
    //inputData.show()

    val predictions = List(modelCategory, modelTheme, modelExecutor).map { model =>
      val result = model.transform(inputData)
      //result.show()
      val predict = result.collect().headOption match {
        case Some(prediction) => prediction.getAs[String]("predictedLabel")
        case None => ""
      }
      logger.info(predict)
      predict
    }

    //spark.stop()
    Prediction(category = predictions.head, theme = predictions(1), executor = predictions(2))
  }

  def toJson(input: Prediction): JsValue = {
    input.toJson
  }

}

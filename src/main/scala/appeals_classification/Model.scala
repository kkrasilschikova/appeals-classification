package appeals_classification

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature._
import org.apache.spark.sql.DataFrame

object Model extends  LazyLogging {
  def createModel(json: DataFrame, column: String): Unit = {
    val tokenizer = new Tokenizer()
      .setInputCol("text")
      .setOutputCol("words")

    val remover = new StopWordsRemover()
      .setInputCol("words")
      .setOutputCol("filtered")
      .setStopWords(StopWordsRemover.loadDefaultStopWords("russian"))

    val tfHasher = new HashingTF()
      .setInputCol("filtered")
      .setOutputCol("rawFeatures")
      .setNumFeatures(39500)

    val idfMaker = new IDF()
      .setInputCol("rawFeatures")
      .setOutputCol("features")

    val labelIndexer = new StringIndexer()
      .setInputCol(column.toLowerCase)
      .setOutputCol("label")
      .fit(json)

    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)

    val Array(trainingData, testData) = json.randomSplit(Array(0.9, 0.1), seed = 1234L)

    val classifier = new NaiveBayes()
      .setFeaturesCol("features")
      .setLabelCol("label")

    val pipeline = new Pipeline()
      .setStages(Array(tokenizer, remover, tfHasher, idfMaker, labelIndexer, classifier, labelConverter))

    val model = pipeline.fit(trainingData)

    val predictions = model.transform(testData)
    predictions.show()

    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")

    logger.info(s"Test set accuracy = ${evaluator.evaluate(predictions)}")

    model.write.overwrite().save(s".\\src\\main\\resources\\model$column")
  }
}

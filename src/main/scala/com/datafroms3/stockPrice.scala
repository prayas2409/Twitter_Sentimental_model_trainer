package com.stockprice

import java.io.File


import org.apache.spark.sql.SparkSession

class stockPrice {

  def remove_string: String => String = _.replaceAll("[_#]", "")

  def getListOfFiles(dir: String): List[String] = {
    val file = new File(dir)
    file.listFiles.filter(_.isFile)
      .filter(_.getName.endsWith(".csv"))
      .map(_.getPath).toList
  }

  def access_s3(path: String, s: SparkSession): Unit = {
    var dataframe = s.read.options(Map("inferSchema"->"true","header" -> "true")).format("com.databricks.spark.csv").load(path)
    import org.apache.spark.sql.functions
    dataframe = dataframe.withColumn("tweet", functions.regexp_replace(dataframe.col("tweet"), ",", ""))
    dataframe = dataframe.withColumn("tweet", functions.regexp_replace(dataframe.col("tweet"), "\\\\", ""))
    dataframe = dataframe.withColumn("tweet", functions.regexp_replace(dataframe.col("tweet"), "\\.", ""))
    dataframe.show()


    val current_dir = System.getProperty("user.dir")
    // code to create folder
    val datafol = current_dir+"/Data"
    try {
      new java.io.File(current_dir+"/model").mkdirs
    } catch {
      case exception: Exception => println("Unable to create folder as ", exception)
    }

    dataframe.repartition(1).write.option("header","true").csv(datafol)



    println("Shown the data")
//    val from_home =
//    dataframe.repartition(1).write.option("header","true").format("csv").save(from_home)
//    println("Written the data into file")


    val filenames = getListOfFiles(current_dir+"/Data/")
    println(filenames(0))
    val out_path = current_dir+"/model/model.pkl"
    val pyfile = "/home/admin1/IdeaProjects/Week10-twitter_scala/src/main/python/twitter_pos_neg_train.py"

    s.sparkContext.addFile(pyfile)
    val paths = s.sparkContext.makeRDD(List(filenames(0)+" "+out_path),1)
//    println(paths.collect().flatMap(_.split(" ")))
    val returned = paths.pipe(pyfile)
    println("got path")
    returned.collect().foreach(println)

  }
}

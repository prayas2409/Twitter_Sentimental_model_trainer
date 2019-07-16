package com.datafroms3

import com.stockprice.stockPrice
import org.apache.spark.sql.SparkSession

object read_from_s3 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("twitter_model_maker").master("local[*]").getOrCreate()
    val obj = new stockPrice()
    obj.access_s3("/home/admin1/IdeaProjects/Week10-twitter_scala/src/main/Data/train.csv",spark)

  }

}

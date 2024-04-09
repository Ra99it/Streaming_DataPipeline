import org.apache.spark.sql.SparkSession

class HotelLog_Streaming {
  val time = java.time.LocalDate.now().minusDays(1)

  val spark = SparkSession
    .builder()
    .config("spark.jars", "/sjm/spark3/mysql-connector-java-5.1.49.jar")
    .config("spark.home", "/sjm/spark3")
    .config("spark.sql.warehouse.dir", "hdfs:///sjm/sjm_warehouse_mysql_5.7")
    .config("spark.useHiveContext", true)
    .config("spark.sql.catalogImplementation", "hive")
    .config("spark.executor.memory", "6g")
    .config("spark.executor.cores", "2")
    .config("spark.driver.cores", "1")
    .config("spark.driver.memory", "6g")
    .config("spark.executor.instances", "3")
    .enableHiveSupport()
    .master("yarn")
    .appName("HotelLogETL")
    .getOrCreate()

  val df_stream_kafka = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "kafka-cluster-01:9092,kafka-cluster-02:9092,kafka-cluster-03:9092")
    .option("subscribe", "hotellogs")
    .load()


  val df_stream_value = df_stream_kafka
    .selectExpr("CAST(value AS STRING)")
    .select("value")

  val df_stream_hotellogs = df_stream_value
    .toDF("hotellogs")

  import org.apache.spark.sql.streaming.Trigger
  import scala.concurrent.duration._


  val query_df_stream_hotellog_hdfs_text = df_stream_hotellogs
    .writeStream
    .trigger(Trigger.ProcessingTime(2.minutes))
    .outputMode("append")
    .format("text")
    .option("path", "hdfs://spark-master-01:9000/sjm/data/hotellogs/" + time)
    .option("checkpointLocation", "hdfs://spark-master-01:9000/checkpoint/structured_streaming/logs/hotellogs/" + time)
    .queryName("query_df_stream_hotellogs_hdfs_text")
    .start()

  println(query_df_stream_hotellog_hdfs_text.status)
  query_df_stream_hotellog_hdfs_text.stop()

  spark.stop()
}

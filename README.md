# Streaming_DataPipeline
## 클러스터 및 서버 정보
<b>1. Hadoop and Spark with Cluster </b>
|인스턴스 이름|성능|기술|
|------|---|---|
|spark-master-01|m5a.xlarge|Hadoop hdfs, yarn, Spark, mysql|
|spark-worker-01|m5a.xlarge|Hadoop hdfs, yarn, Spark, mysql|
|spark-worker-02|m5a.xlarge|Hadoop hdfs, yarn, Spark, mysql, cassandra|
|spark-worker-03|m5a.xlarge|Hadoop hdfs, yarn, Spark, mysql|

<b>2. Kafka Cluster</b>
|인스턴스 이름|성능|기술|
|------|---|---|
|de-kafka-cluster-1|t2.xlarge |Kafka, fluentd|
|de-kafka-cluster-2|t2.xlarge |Kafka, fluentd|
|de-kafka-cluster-3|t2.xlarge |Kafka, fluentd|

## 데이터 파이프라인 아키텍처
![streaming_1 drawio (1)](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/4ee9a2ae-e449-435e-8339-076eee53dd1e)


## 가상 시나리오
API로 로그를 출력해 가상의 시나리오를 구성했습니다.

### 이상탐지를 위한 GameLog Streaming

| Command | Description |
| --- | --- |
| 참가 인원 | 6명 |
| 게임 시간 | 20분 |
| 수집할 Log| ip, datetime, account, gametime, method, deadcount, inputkey,status, x, y |
| 최종 Log 갯수 | 약 4900개 |

6명의 가상의 사용자를 생성하고, 20분동안 해당 게임세션의 Log를 생성합니다. <br>

로그는 Ip, DateTime, Account, GameTime,Method, Deadcount, InputKey,Status, X, Y를 생성하고, Kafka로 전송하게 됩니다. <br>

DateTime은 해당 로그가 생성된 시간을 수집하고, GameTime은 해당 게임의 진행시간을 수집하게 됩니다. <br>

사용자의 Method는 move와 wait가 있는데, move일 시, 마우스의 x좌표와 y좌표를 수집하게 되고 <br>

wait일 시, 해당 유저는 멈춰있는 상태라고 지정했습니다. <br>

Move 메소드는 정말 높은 확률로 일정한 마우스 좌표로 움직이며, 낮은 확률로 좌표가 변동이 크게 마우스 좌표가 움직입니다. <br>

좌표가 불규칙적으로 움직이는 것으로 이상을 탐지하는 것이 아니라, Datetime과 GameTime과 비교해서 너무 짧은 시간에 <br>

마우스의 좌표가 불규칙 하게 변동폭이 크다면, 이상을 의심할 수 있다고 판단했습니다.

inputkey는 해당 유저가 키를 입력할 때 수집되는 로그입니다. 해당 API는 "리그오브레전드"를 참조해서 높은 확률로 q,w,e,r,d,f를 입력받고 <br>

낮은 확률로 alt, tab의 키를 입력하도록 했습니다. 게임 중 일반적이지 않은 키 입력은 중요한 판단 중 하나라고 생각했습니다. <br>

status는 해당 유저가 게임 상에서 살아있는 상태면 0, 죽어있는 상태면 1로 지정하고 deadcount를 증가시키도록 했습니다. <br>

20분 동안 약 4900개의 로그 데이터가 수집되었고, 원본 Log 데이터는 Data Lake인 hadoop hdfs로 저장하며, 전처리 후 효율적으로 <br>

Scala, Python, Java에서 활용할 수 있도록 Cassandra로 Data Mart로 제공했습니다.

-------


### Hotel_Web_Log_Streaming
| Command | Description |
| --- | --- |
| 참가 인원 | 100명 |
| 게임 시간 | 20분 |
| 출력 로그 | ... |
| 최종 Log 갯수 | 약 26000개 |

--------

### Image_Streaming


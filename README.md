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


# Log API

## 이상탐지를 위한 GameLog Streaming

| Command | Description |
| --- | --- |
| 참가 인원 | 6명 |
| 게임 시간 | 20분 |
| 최종 수집 된 Log 갯수 | 약 4900개 |

6명의 가상의 사용자를 생성하고, 20분동안 6명의 사용자의 로그를 생성합니다.<br>
한창 좋아했던 게임인 리그 오브 레전드를 참조했습니다.
<br>

### 가상 시나리오

|Azir|Viktor|Orianna|Vex|Ryze|Zilean|
|----|---|---|---|---|---|
|![Azir](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/5b88072a-4f8d-4679-a941-765514e72ffc)|![Viktor](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/093c0efd-a463-464e-9b25-a9e3de626933)|![Orianna](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/e2964948-b8ae-4293-a5d8-a07db6cde62e)|![Vex](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/0138cf0a-47c4-4034-a60c-1f1bbf2966ee)|![Ryze](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/d69dca34-027a-4300-9266-61c0a6ae88bf)|![Zilean](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/38ac02e7-07f6-4d97-a489-a61990384340)|
|일반유저|의심유저|일반유저|일반유저|일반유저|일반유저|

해당 시나리오에선, Viktor를 선택한 사용자가 외부 프로그램을 의심할 수 있는 유저로 지정했습니다.
다른사용자와의 차이점은 다른 사용자에 비해 마우스의 좌표의 이상값의 확률이 많아집니다.
<br>

```
생성되는 로그
  {        
            "datetime":"2024-03-24T11:25:29.845Z",
            "method":"\/move",
            "ingametime":"0:0",
            "ip":"192.168.0.50",
            "x":"277",
            "y":"-351",
            "deathCount":"0",
            "id":"b405fd3c-785b-4e42-a825-52a75f3d82a4",
            "inputkey":"b",
            "account":"testAccount_56",
            "champion":"vex",
            "status":"0"
  } 
```

로그는 Ip, DateTime, Account, GameTime,Method, Deadcount, InputKey,Status, X, Y, Champion을 생성하고, Kafka로 전송하게 됩니다. <br>

**DateTime**은 해당 로그가 생성된 시간을 수집하고, **GameTime**은 해당 게임의 진행시간을 수집하게 됩니다. <br>

| Method | Description |
| --- | --- |
| /move | 마우스를 클릭 할 때, 출력되는 메소드입니다. 메소드 호출 시 마우스의 x,y 좌표를 저장합니다. |
| /getItem | 아이템을 샀을 때 호출되는 메소드입니다. 한명 당 6번만 호출됩니다. |
| /wait | 사용자가 멈춰있는 상태입니다. |

**Move** 메소드는 정말 높은 확률로 일정한 마우스 좌표로 움직이며, 낮은 확률로 좌표가 변동이 크게 마우스 좌표가 움직입니다. <br>

좌표가 불규칙적으로 움직이는 것으로 이상을 탐지하는 것이 아니라, **Datetime**과 **GameTime**과 비교해서 너무 짧은 시간에 <br>

마우스의 좌표가 불규칙 하게 변동폭이 크다면, 이상을 의심할 수 있다고 판단했습니다.

**inputkey**는 해당 유저가 키를 입력할 때 수집되는 로그입니다. 해당 API는 "리그오브레전드"를 참조해서 높은 확률로 q,w,e,r,d,f를 입력받고 <br>

낮은 확률로 alt, tab의 키를 입력하도록 했습니다. 게임 중 일반적이지 않은 키 입력은 중요한 판단 중 하나라고 생각했습니다. <br>

**status**는 해당 유저가 게임 상에서 살아있는 상태면 0, 죽어있는 상태면 1로 지정하고 **deadcount**를 증가시키도록 했습니다. <br>

20분 동안 약 4900개의 로그 데이터가 수집되었고, 원본 Log 데이터는 Data Lake인 hadoop hdfs로 저장하며, 전처리 후 효율적으로 <br>

Scala, Python, Java에서 활용할 수 있도록 Storage인 Cassandra로 Data Mart로 제공했습니다.

```
최종적으로 저장 될 로그
  {        
            "datetime":"2024-03-24T11:25:29.845Z",
            "method":"\/move",
            "ingametime":"0:0",
            "ip":"192.168.0.50",
            "x":"277",
            "y":"-351",
            "x+y" : "123",
            "deathCount":"0",
            "id":"b405fd3c-785b-4e42-a825-52a75f3d82a4",
            "inputkey":"b",
            "account":"testAccount_56",
            "champion":"vex",
            "status":"0"
  } 
```

-------


### Hotel_Web_Log_Streaming
| Command | Description |
| --- | --- |
| 참가 인원 | 100명 |
| 게임 시간 | 20분 |
| 수집할 로그 | id, ip, datetime, account, method, gender, age |
| Example Log | {"id": "320ade38-cfa2-42df-8142-b26af760bb29","ip": "192.168.0.111","datetime": "2024-03-21T16:38:02.344Z","account": "testAccount_36","method": "/","status": "0","gender": "female","age": "60-100"} |
| 최종 Log 갯수 | 약 26000개 |

--------

### Image_Streaming


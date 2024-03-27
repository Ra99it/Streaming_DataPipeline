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
            "status":"0",
            "buyItem" : 1
  } 
```

-------


## Hotel_Web_Log_Streaming
| Command | Description |
| --- | --- |
| 참가 인원 | 100명 |
| 게임 시간 | 20분 |
| 최종 Log 갯수 | 약 13000개 |

100명의 사용자를 생성하고, 20분동안 100명의 로그를 생성합니다.<br>
해당 API를 제작할 때, 야놀자(https://www.yanolja.com/) 를 참조해서 로그를 작성했습니다.

### 가상 시나리오

![1](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/c485de40-4d1b-4643-b835-f890b3b5a8d0)

먼저, 사용자가 메인 화면으로 들어오면서 숙소를 찾는 과정을 가상 시나리오로 선정했습니다.

```
원본 로그
{        
  {
      "id": "f19dc589-7856-4bae-b5ba-fc9e0bc65aa5",
      "ip": "192.168.0.144",
      "datetime": "2024-03-26T16:34:00.450Z",
      "account": "testAccount_28",
      "method": "/",
      "status": "1",
      "gender": "female",
      "age": "20-29"
  }
} 
```
기본적인 회원의 정보를 수집합니다. **Ip**와 **Account**, **Gender**, **Age** 등등 <br>

**DateTime**은 로그가 생성된 시간을 저장하고, **Method**는 다음과 같이 지정하고 있습니다. <br>

| Method | Description |
| --- | --- |
| / | 사용자가 메인 페이지에 처음 들어오거나, 돌아올 때 호출하는 메소드입니다. |
| /sub-home/hotel | 메인 페이지 상단에 호텔/리조트를 클릭 시 호출되는 메소드입니다. |
| /sub-home/pension | 메인 페이지 상단에 펜션/풀빌라를 클릭 시 호출되는 메소드입니다. |
| /sub-home/redidence | 메인 페이지 상단에 가족형숙소를 클릭 시 호출되는 메소드입니다. |
| /sub-home/motel | 메인 페이지 상단에 모텔을 클릭 시 호출되는 메소드입니다. |
| /flights | 메인 페이지 상단에 항공을 클릭 시 호출되는 메소드입니다. |
| /sub-home/global | 메인 페이지 상단에 해외숙소를 클릭 시 호출되는 메소드입니다. |
| /sub-home/transportation | 메인 페이지 상단에 교통을 클릭 시 호출되는 메소드입니다. |
| /leisure | 메인 페이지 상단에 레저/티켓을 클릭 시 호출되는 메소드입니다. |
| /places/숫자 | 사용자가 숙소를 클릭 했을 때 호출하는 메소드입니다. |
| /buy/places/숫자 | 사용자가 숙소를 구매 또는 예약했을 때 호출하는 메소드입니다. |
| /refund | 사용자가 숙소 및 예약을 취소, 환불 했을 때 호출하는 메소드입니다. |

사용자가 숙소를 예약했을 때 **status**의 값은 1을 가지게 됩니다. **status**가 1의 값을 가지고 있지 않을 땐, <br>

*/refund* 메소드가 발생하지 않도록 하였고, 사용자가 예약을 했으면, 즉 **status**의 값이 1이 되었을 때 낮은 확률적으로 환불을 진행하게 됩니다.<br>

추가적으로 예시로 /buy/places/31 메소드가 호출 됐을 경우, **buyNum**의 컬럼을 새로 만들고 값을 31로 지정하고, <br>

*/places/31*의 메소드가 호출이 되면, **clickNum**의 컬럼을 만들고 31으로 저장되게 됩니다. 마지막 메인페이지 상단의 여러 메뉴를 클릭 시 <br>

hotel을 클릭했는지 pension을 클릭했는지 값으로 남겨두게 했습니다. <br>

20분 동안 생성해서 약 13000개의 로그가 생성되었고, 전처리 후 Cassandra로 저장하였습니다. <br>

```
최종적을 저장 될 로그
{        
  {
      "id": "f19dc589-7856-4bae-b5ba-fc9e0bc65aa5",
      "ip": "192.168.0.144",
      "datetime": "2024-03-26T16:34:00.450Z",
      "account": "testAccount_28",
      "method": "/",
      "status": "1",
      "gender": "female",
      "age": "20-29",
      "buyNum" : "0",
      "clickNum" : "0",
      "catagory_subhome" : "null"
  }
} 
```

--------

### Image_Streaming


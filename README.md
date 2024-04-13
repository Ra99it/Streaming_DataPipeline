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

<b>3. Airflow Cluster</b>
|인스턴스 이름|성능|기술|
|------|---|---|
|airflow-master-01|t3a.xlarge |Airflow|
|airflow-worker-01|t3a.small |Airflow|
|airflow-worker-02|t3a.small |Airflow|
|airflow-mysql-01|t3a.large |MYSQL|
|airflow-redis-01|t3a.micro |Redis|

**4. 노트북 환경은 Zeppelin을 사용하고 있습니다**

## 데이터 파이프라인 아키텍처
![streaming_1 drawio (1)](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/4ee9a2ae-e449-435e-8339-076eee53dd1e)


# Log API

## LOL log generator for anomaly detection

| Command | Description |
| --- | --- |
| 참가 인원 | 100명 |
| 게임 시간 | 60분 |
| 최종 수집 된 Log 갯수 | 약 25000개 |

*한창 좋아했던 게임인 리그 오브 레전드를 참조했습니다.* <br>

마우스의 X,Y 좌표값을 이상 탐지 하기 위해 API를 제작했습니다.

<br>

### 가상 시나리오

|Azir|Viktor|Orianna|Vex|Ryze|Zilean|
|----|---|---|---|---|---|
|![Azir](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/5b88072a-4f8d-4679-a941-765514e72ffc)|![Viktor](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/093c0efd-a463-464e-9b25-a9e3de626933)|![Orianna](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/e2964948-b8ae-4293-a5d8-a07db6cde62e)|![Vex](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/0138cf0a-47c4-4034-a60c-1f1bbf2966ee)|![Ryze](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/d69dca34-027a-4300-9266-61c0a6ae88bf)|![Zilean](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/38ac02e7-07f6-4d97-a489-a61990384340)|
|일반유저|의심유저|일반유저|일반유저|일반유저|일반유저|

|Ari|Anivia|AurelionSol|Lux|Malzahar|Neeko|
|----|---|---|---|---|---|
|![Ahri](https://github.com/Ra99it/Before-Big-Data-Platform/assets/122541545/adb183bd-d429-43a9-bc5d-edb0f0c80017)|![Anivia](https://github.com/Ra99it/Before-Big-Data-Platform/assets/122541545/7a4aac79-9ce0-4971-9f6d-4bcf222d44da)|![AurelionSol](https://github.com/Ra99it/Before-Big-Data-Platform/assets/122541545/486bfc58-2fb1-4cfd-a378-57c4d2eb63a7)|![Lux](https://github.com/Ra99it/Before-Big-Data-Platform/assets/122541545/0224f2e8-4af1-4ec8-8362-23b8a74d1417)|![Malzahar](https://github.com/Ra99it/Before-Big-Data-Platform/assets/122541545/e0d279b0-0f01-4178-a7bd-6cbddc5a1d6a)|![Neeko](https://github.com/Ra99it/Before-Big-Data-Platform/assets/122541545/1e67d8e7-fcb7-4a11-8b4e-a6d785bac2bf)|
|일반유저|일반유저|일반유저|일반유저|일반유저|일반유저|

|Sylas|Syndra|Taliyah|Xerath|Vladimir|
|----|---|---|---|---|
|![Sylas](https://github.com/Ra99it/Before-Big-Data-Platform/assets/122541545/54afe86f-6b0d-4166-9324-f40b71e12892)|![Syndra](https://github.com/Ra99it/Before-Big-Data-Platform/assets/122541545/5798ae4b-e5fe-457f-99a3-2ae2fd630d3d)|![Taliyah](https://github.com/Ra99it/Before-Big-Data-Platform/assets/122541545/9d88682a-ae9a-4192-bb4a-6d90912861d1)|![Xerath](https://github.com/Ra99it/Before-Big-Data-Platform/assets/122541545/80fce79d-fdd3-4cdd-b97b-2ae8524299e1)|![Vladimir](https://github.com/Ra99it/Before-Big-Data-Platform/assets/122541545/37d209d4-c05d-4f64-9a63-56369b5ce885)|
|일반유저|일반유저|일반유저|일반유저|일반유저|일반유저|

----
1. 해당 시나리오에서의 플레이어는 100명입니다.
2. 10명의 인원제한을 가진 방을 생성합니다.
3. 총 10개의 방을 생성합니다.
4. 챔피언(= 캐릭터)는 랜덤으로 생성됩니다.
5. Viktor를 제외한 다른 챔피언은 정상적인 플레이어입니다.
6. Vikttor를 플레이하는 플레이어는 외부 프로그램을 사용하고 있을 거 같은 의심 유저입니다.
7. Viktor 사용자는 정상적인 사용자보다 마우스 좌표 이상값의 확률이 정상적인 사용자보다 많습니다.
8. 해당 시나리오는 5대5가 아닌 6명이 각각 개인전을 한다는 설정으로 로그를 생성하고 수집합니다.
----
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

데이터는 분석용 데이터와 학습용 데이터로 최종적으로 저장하게 됩니다.

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

*야놀자(https://www.yanolja.com/) 를 참조했습니다.*
사용자가 숙소를 찾는 과정에서, 로그를 수집하고 다양한 로그 분석을 위해 API를 제작했습니다.

### 가상 시나리오

![1](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/c485de40-4d1b-4643-b835-f890b3b5a8d0)

---
1. 해당 사이트를 이용하는 사용자의 수는 100명입니다.
2. 100명의 사용자는 약 20분동안 사이트를 이용하게 됩니다.
3. 사용자는 숙소를 예약할 수 있으며, 환불 할 수 있습니다.
4. 100명의 사용자가 숙소를 찾는 과정에서 로그를 생성하고 수집합니다.
---

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

최종적으로 분석용 데이터로 저장됩니다. <br>

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

## LostArkCommander, 로스트아크 군단장 레이드 Log Generator

| Command | Description |
| --- | --- |
| 참가 인원 | 8명 |
|방 수| 5 개 (총 40명)|
| 게임 시간 | 20분 |
| 최종 Log 갯수 | 약 3500개 |

*로스트아크의 군단장 레이드를 참조했습니다.*

인게임의 플레이어의 행동 분석과 로그 분석을 위해 제작하게 되었습니다.

### 가상시나리오

![OfxBYUJajDYsTexb9bhcrPQ94U4dtR1UL-E6GNgV9RUB_TiSwFFJoAQQfq8_a9DPORg8lzzbdTGQOqK5Nd8Msg4sbnf0br5mu0wPLrzEQR2o0bOQF_vJSVkHIiSoJsmdVUo_5PNUS-zDJjYPwrj1yw](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/f39bcc1a-8ac5-4fd6-b13c-6c1970d34632)


  |쿠 크 세 이 튼|일 리 아 칸|카 멘|아 브 렐 슈 드|비 아 키 스|발 탄|
  |:----:|:---:|:-------:|:---:|:---:|:---:|
  |1445|1600|1630|1560|1460|1445|
  |10분|15분|20분|13분|10분|10분|

-----

1. 해당 시나리오에서는 5개의 방을 생성합니다.
2. 방의 참가인원은 8명입니다.
3. 방마다 랜덤으로 군단장 레이드가 선정이 됩니다. 
4. 각각의 군단장마다 난이도가 다르고, 레이드 시간이 다릅니다
5. 참가인원은 각 레이드 시간안에 모두 죽지 않아야 하고, 한명이라도 살아있다면 성공으로 간주합니다.
6. 방을 참가하고 있는 인원의 정보 및 행동 로그를 생성하고 수집합니다.

-----

```
Log
{
    "sessionID": "2ca61813-cdd8-4661-a56c-4165a714fde5",
    "startTime": "2024-03-28T00:20:03.006",
    "gametime": "0:1",
    "success": "0",
    "Boss": {
      "difficulty": "10",
      "name": "Kamen",
      "endTime": "1200"
    },
    "User": {
      "ip": "192.168.0.168",
      "class": "Demonic",
      "account": "testAccount_53"
    },
    "method": "/move",
    "x": "-121",
    "y": "295",
    "inputkey": "space",
    "status": "0"
}
```

**SessionID**는 생성된 방의 ID를 담고, **StartTime**은 레이드가 시작 된 시간을 담은 정보입니다 <br>

**GameTime**은 현재 방의 진행된 시간을 담고있고, **Success**는 해당 방의 레이드가 최종적으로 성공했는지, 실패했는지 알 수 있는 로그입니다. <br>

보스마다 지정 된 시간 안에 플레이어가 살아있다면, **Success**는 1의 값을 가지게 됩니다. **Boss**는 보스의 정보를 담고있습니다. 이름 및 시간과, 난이도 등등 <br>

**User**은 참가한 인원의 **Ip**와 **Class**, **Account**를 담은 로그입니다. **Method**는 플레이어가 호출한 메소드 정보를 남긴 로그입니다. <br>

**Class**는 2024.03월 기준 출시 된 클래스를 기준으로 랜덤으로 지정하게 됩니다. <br>

| Method | Description |
| --- | --- |
| /move | 마우스를 클릭 할 때, 출력되는 메소드입니다. 메소드 호출 시 마우스의 x,y 좌표를 저장합니다. |
| /chat | 플레이어가 채팅입력을 했을 때 출력되는 메소드입니다. |
| /wait | 사용자가 멈춰있는 상태입니다. |

마우스의 **X**,**Y**좌표를 저장하고, **InputKey**는 플레이어가 입력한 키를 남긴 로그입니다. **Status**는 해당 플레이어가 레이드 도중 <br>

사망했는지, 살아있는지 정보를 담고있는 로그입니다. <br>

이렇게 수집된 데이터는 Data Warehouse로 StartTime과 SessionID, Account 세가지로 Partition 나누어 효율적으로 저장하게 됩니다.

최종적으로 분석용 데이터와 학습용 데이터로 저장됩니다.

-----------

## 광고 API

| Command | Description |
| --- | --- |
| 참가 인원 | 1000명 |
| 게임 시간 | 60분 |
| 최종 수집 된 Log 갯수 | 약 1000개 |

*사람들의 행동 분석 및 학습 또는 BI를 위해 제작했습니다*

-----
1. 사용자는 1000명으로 구성됩니다.
2. 사용자는 광고를 보지 않거나, 보거나, 광고를 클릭하게 됩니다.
3. 광고를 보고있는 시간과 클릭한 정보를 저장하게 됩니다.
4. 사용자는 클릭을 했을 시, 약 1분 동안의 클릭은 집계되지 않습니다.
-----

```
원본 로그
{
  "Ad": {
    "start_time": "2024-03-31",
    "ad_id": "76015c91-f1a9-4e4c-9a4c-c9130b7d27fe",
    "ad_name": "리그오브레전드",
    "genre": "game",
    "end_time": "2024-04-15",
    "click_count": "0",
    "viewing_time": "0",
    "ad_explain": "리그오브레전드와 관련된 광고입니다."
  },
  "User": {
    "view": "true",
    "gender": "female",
    "total_time": "0",
    "click": "false",
    "account": "testAccount_11",
    "age": "36"
  }
}
```
**Ad**는 광고의 정보를 담은 Key값 입니다. <br>

**Start_time, Ad_id, ad_name, genre, endtime, click_count, viewing_time, ad_explain**의 정보를 갖고있습니다. <br>

**Start_time**과 **End_time**은 해당 광고를 게시하는 기간을 저장합니다 <br>

**AD_id**는 광고의 ID 값을 저장합니다. <br>

**Genre**는 광고의 장르 정보를 담고 있습니다. <br>

**AD_explain**은 광고의 간단한 설명을 담고 있습니다. <br>

**Click_count**는 해당 광고가 얼마나 클릭 됐는지 집계된 count를 저장하게 됩니다. <br>
해당 API에서는 광고클릭 하고 약 1분의 시간동안 중복 클릭은 집계되지 않습니다. <br>

**viewing_time**은 사용자가 광고를 본 시간을 집계한 정보를 저장하고 있습니다. <br>

**User**은 사용자의 관련된 정보를 저장한 값입니다. <br>

**account, age, gender, click, view, total_time** 정보를 담고 있습니다. <br>

각각 계정명과 나이, 성별을 저장하고, 사용자가 광고를 클릭 했을 때 click 정보를 저장하게 됩니다. <br>

**view**는 사용자가 광고를 시청하고 있을 때 저장되는 값이고, total_time은 해당 사용자의 총 광고 시간을 담고있습니다. <br>

최종적으로 분석용 데이터로 저장됩니다. <br>

```
Data
{
  "Ad": {
    "start_time": "2024-03-31",
    "ad_id": "76015c91-f1a9-4e4c-9a4c-c9130b7d27fe",
    "ad_name": "리그오브레전드",
    "genre": "game",
    "end_time": "2024-04-15",
    "click_count": "0",
    "viewing_time": "0",
    "ad_explain": "리그오브레전드와 관련된 광고입니다."
  },
  "User": {
    "view": "true",
    "gender": "female",
    "total_time": "0",
    "click": "false",
    "account": "testAccount_11",
    "age": "36"
  }
}
```

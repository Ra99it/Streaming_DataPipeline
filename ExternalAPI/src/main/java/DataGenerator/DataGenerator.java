package DataGenerator;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class DataGenerator implements Runnable{
    private CountDownLatch latch;
    private String ipAddr;
    private String account;
    private String sessionID;
    private int durationSeconds;

    private Random rand;

    private final long MINIMUM_SLEEP_TIME = 5;
    private final long MAXIMUM_SLEEP_TIME = 60 * 50;
    private final String TOPIC_NAME = "gamelogs";

    public DataGenerator(CountDownLatch latch, String ipAddr, String account,String sessionID, int durationSeconds) {
        this.latch = latch;
        this.ipAddr = ipAddr;
        this.account = account;
        this.sessionID = sessionID;
        this.durationSeconds = durationSeconds;
        this.rand = new Random();
    }

    @Override
    public void run() {
        System.out.println("Starting log generator (ipAddr=" + ipAddr +", account="+ account +", sessionID=" + sessionID + ", durationSeconds=" + durationSeconds);

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-cluster-01:9092,kafka-cluster-02:9092,kafka-cluster-03:9092");
        //props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "spark-worker-01:9092,spark-worker-02:9092,spark-worker-03:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "SoloGameDataGenerator");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        long startTime = System.currentTimeMillis();
        Integer deathCount = 0;

        while (isDuration(startTime)){
            long sleepTime = MINIMUM_SLEEP_TIME + Double.valueOf(rand.nextDouble() * (MAXIMUM_SLEEP_TIME)).longValue();

            try{
                Thread.sleep(sleepTime);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            String method = getMethod();
            OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneId.of("UTC"));
            long endTime = System.currentTimeMillis();

            long runTime_seconds = (endTime-startTime)/1000;
            long runTime_min = runTime_seconds/60;
            String finalTime = runTime_min+":"+(runTime_seconds%60);
            String uuid = getUUID();
            Integer x_direction = getX();
            Integer y_direction = getY();
            String key = getKey();
            Integer status = getStatus();

            if (method.equals("/move")) {
                if(status == 1) {

                    String log = String.format(
                            "{" +
                                    "\"id\": \"%s\"," +
                                    "\"ip\": \"%s\"," +
                                    "\"account\": \"%s\"," +
                                    "\"method\": \"%s\"," +
                                    "\"datetime\": \"%s\"," +
                                    "\"x\": \"%s\"," +
                                    "\"y\": \"%s\"," +
                                    "\"inputkey\": \"%s\"," +
                                    "\"status\": \"%s\"," +
                                    "\"deathCount\": \"%s\"," +
                                    "\"ingametime\": \"%s\"" + "}" ,uuid,ipAddr, account,method,offsetDateTime,x_direction,y_direction,key,status, deathCount, finalTime
                    );

                    JSONParser jsonParser = new JSONParser();
                    try {
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(log);
                        System.out.println(jsonObject);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    producer.send(new ProducerRecord<>(TOPIC_NAME, log));

                    status = 0;
                    deathCount += 1;
                }
                String log = String.format(
                        "{" +
                                "\"id\": \"%s\"," +
                                "\"ip\": \"%s\"," +
                                "\"account\": \"%s\"," +
                                "\"method\": \"%s\"," +
                                "\"datetime\": \"%s\"," +
                                "\"x\": \"%s\"," +
                                "\"y\": \"%s\"," +
                                "\"inputkey\": \"%s\"," +
                                "\"status\": \"%s\"," +
                                "\"deathCount\": \"%s\"," +
                                "\"ingametime\": \"%s\"" + "}" ,uuid,ipAddr, account,method,offsetDateTime,x_direction,y_direction,key,status, deathCount, finalTime
                );

                JSONParser jsonParser = new JSONParser();
                try {
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(log);
                    System.out.println(jsonObject);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                producer.send(new ProducerRecord<>(TOPIC_NAME, log));
            }else {
                String log = String.format(
                        "{" +
                                "\"id\": \"%s\"," +
                                "\"ip\": \"%s\"," +
                                "\"account\": \"%s\"," +
                                "\"method\": \"%s\"," +
                                "\"datetime\": \"%s\"," +
                                "\"x\": \"0\"," +
                                "\"y\": \"0\"," +
                                "\"inputkey\": \"0\"," +
                                "\"status\": \"%s\"," +
                                "\"deathCount\": \"%s\"," +
                                "\"ingametime\": \"%s\"" + "}", uuid, ipAddr, account,method,offsetDateTime,status, deathCount, finalTime
                );
                JSONParser jsonParser = new JSONParser();
                try {
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(log);
                    System.out.println(jsonObject);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                producer.send(new ProducerRecord<>(TOPIC_NAME, log));
            }
        }
        //producer.close();
        System.out.println("Stopping log generator (ipAddr=" + ipAddr +", account="+ account +", sessionID=" + sessionID + ", durationSeconds=" + durationSeconds);
        this.latch.countDown();
    }

    private boolean isDuration(long startTime) {
        return System.currentTimeMillis() - startTime < durationSeconds * 1000L;
    }

   private String getMethod() {
       if (rand.nextDouble() > 0.80) {
           return "/wait";
       }else {
           return "/move";
       }
   }

   private int getX(){
        int x = 0;

        if (rand.nextDouble() > 0.99) {
            x = rand.nextInt(2000 - (-2000) +1) + (-2000);
            return x;
        } else {
            x = rand.nextInt(400 - (-400) +1) + (-400);
            return x;
        }
   }

    private int getY(){
        int y = 0;

        if (rand.nextDouble() > 0.99) {
            y = rand.nextInt(2000 - (-2000) +1) + (-2000);
            return y;
        } else {
            y = rand.nextInt(400 - (-400) +1) + (-400);
            return y;
        }
    }

    private String getKey() {
        if (rand.nextDouble() > 0.95) {
            String[] arrKey = new String[]{"alt", "tab", "alt+tab", "esc", "shift", "enter"};
            int num = rand.nextInt(arrKey.length);
            String key = arrKey[num];

            return key;
        }else if (rand.nextDouble() > 0.75){
            String[] arrKey = new String[]{"d", "f", "null"};
            int num = rand.nextInt(arrKey.length);
            String key = arrKey[num];

            return key;
        } else {
            String[] arrKey = new String[]{"q", "w", "e", "r", "space", "b"};
            int num = rand.nextInt(arrKey.length);
            String key = arrKey[num];

            return key;
        }
    }

    private int getStatus() {
        if (rand.nextDouble() > 0.99) {
            return 1;
        }else {
            return 0;
        }
    }

    private String getInGameTime() {
        return  "0";
    }

    private String getUUID() {
        String uuid = String.valueOf(UUID.randomUUID());
        return uuid;
    }
}

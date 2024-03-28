package lodgmentDataGenerator;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class LodgmentDataGenerator implements Runnable{
    private CountDownLatch latch;
    private String ipAddr;
    private String account;
    private String sessionID;
    private int durationSeconds;
    private String age;
    private String gender;
    private Random rand;
    private int status = 0;
    private final long MINIMUM_SLEEP_TIME = 100;
    private final long MAXIMUM_SLEEP_TIME = 60 * 300;
    private final String TOPIC_NAME = "hotellogs";
    public LodgmentDataGenerator(CountDownLatch latch, String ipAddr, String account,String gender,String age,String sessionID, int durationSeconds) {
        this.latch = latch;
        this.ipAddr = ipAddr;
        this.account = account;
        this.gender = gender;
        this.age = age;
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

        while (isDuration(startTime)){
            long sleepTime = MINIMUM_SLEEP_TIME + Double.valueOf(rand.nextDouble() * (MAXIMUM_SLEEP_TIME)).longValue();

            try{
                Thread.sleep(sleepTime);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            String method = getMethod();
            OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneId.of("UTC"));
            String uuid = getUUID();


            if (method.matches("(\\/buy\\/places\\/\\d{1,3})")){
                status = 1;
                OutLog(uuid, ipAddr, offsetDateTime, account, method, status, gender, age, producer);
            } else  if (status == 1) {
                if (rand.nextDouble() > 0.98) {
                    status = 0;
                    OutLog(uuid, ipAddr, offsetDateTime, account, "/fefund", status, gender, age, producer);
                } else {
                    OutLog(uuid, ipAddr, offsetDateTime, account, method, status, gender, age, producer);
                }
            } else if (gender.equals("female")) {
                if (rand.nextDouble() > 0.94) {
                    status = 1;
                    OutLog(uuid, ipAddr, offsetDateTime, account, "/buy/places/"+rand.nextInt(50), status, gender, age, producer);
                } else {
                    OutLog(uuid, ipAddr, offsetDateTime, account, method, status, gender, age, producer);
                }
            } else {
                OutLog(uuid, ipAddr, offsetDateTime, account, method, status, gender, age, producer);
            }
        }
        System.out.println("Stopping log generator (ipAddr=" + ipAddr +", account="+ account +", sessionID=" + sessionID + ", durationSeconds=" + durationSeconds);
        this.latch.countDown();
    }

    private  void OutLog(String uuid, String ipAddr, OffsetDateTime offsetDateTime, String account, String method, Integer status, String gender, String age, KafkaProducer<String, String> producer) {
        String log = String.format(
                "{" +
                        "\"id\": \"%s\"," +
                        "\"ip\": \"%s\"," +
                        "\"datetime\": \"%s\"," +
                        "\"account\": \"%s\"," +
                        "\"method\": \"%s\","+
                        "\"status\": \"%s\","+
                        "\"gender\": \"%s\","+
                        "\"age\": \"%s\""+ "}" ,uuid, ipAddr, offsetDateTime,account,method, status,gender,age
        );

        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(log);
            System.out.println(jsonObject);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        //producer.send(new ProducerRecord<>(TOPIC_NAME, log));
    }

    private boolean isDuration(long startTime) {
        return System.currentTimeMillis() - startTime < durationSeconds * 1000L;
    }

    private String getMethod() {
        if (rand.nextDouble() > 0.7) {
            if (rand.nextDouble() > 0.85) {
                return "/sub-home/hotel";
            } else if (rand.nextDouble() > 0.85) {
                return "/sub-home/pension";
            } else if (rand.nextDouble() > 0.85) {
                return "/sub-home/residence";
            } else if (rand.nextDouble() > 0.85) {
                return "/sub-home/motel";
            } else if (rand.nextDouble() > 0.875) {
                return "/flights";
            } else if (rand.nextDouble() > 0.875) {
                return "/sub-home/global";
            } else if (rand.nextDouble() > 0.875) {
                return "/sub-home/transportation";
            } else{
                return "/leisure";
            }
        } else if (rand.nextDouble() > 0.7){
            if (rand.nextDouble() > 0.97) {
                return "/buy/places/"+rand.nextInt(50);
            } else {
                return "/places/"+rand.nextInt(50);
            }
        } else{
            return "/";
        }
    }

    private String getUUID() {
        String uuid = String.valueOf(UUID.randomUUID());
        return uuid;
    }

}

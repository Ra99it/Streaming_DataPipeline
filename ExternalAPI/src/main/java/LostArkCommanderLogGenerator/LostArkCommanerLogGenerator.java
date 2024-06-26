package LostArkCommanderLogGenerator;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class LostArkCommanerLogGenerator implements Runnable{
    private CountDownLatch latch;
    private String ipAddr;
    private String account;
    private String sessionID;
    private String classname;
    private Integer success;
    private int durationSeconds;
    private String sessionRoomID;
    private String[] bossInfo;

    private Random rand;
    private final long MINIMUM_SLEEP_TIME = 50;
    private final long MAXIMUM_SLEEP_TIME = 60 * 150;

    private final String TOPIC_NAME = "lostarklogs";

    public LostArkCommanerLogGenerator(CountDownLatch latch, String ipAddr, String account, String classname,String sessionID, int durationSeconds, String sessionRoomID, String[] bossInfo, Integer success){
        this.latch = latch;
        this.ipAddr = ipAddr;
        this.account = account;
        this.classname = classname;
        this.sessionID = sessionID;
        this.durationSeconds = durationSeconds;
        this.sessionRoomID = sessionRoomID;
        this.bossInfo = bossInfo;
        this.success = success;
        this.rand = new Random();
    }
    @Override
    public void run() {
        System.out.println( account+"님이 참가했습니다. 직업: "+ classname + ",방 번호 :"+sessionRoomID+", 군단장: "+bossInfo[0]);

        Properties props = new Properties();
        //props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-cluster-01:9092,kafka-cluster-02:9092,kafka-cluster-03:9092");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "spark-worker-01:9092,spark-worker-02:9092,spark-worker-03:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "LostArkCommanderLogGenerator");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        long startTime = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        Integer status = 0;

        while(isDuration(startTime)){
            long sleepTime = MINIMUM_SLEEP_TIME + Double.valueOf(rand.nextDouble() * (MAXIMUM_SLEEP_TIME)).longValue();

            try{
                Thread.sleep(sleepTime);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            long endTime = System.currentTimeMillis();
            long runTime_seconds = (endTime-startTime)/1000;
            long runTime_min = runTime_seconds/60;
            String finalTime = runTime_min+":"+(runTime_seconds%60);

            String method = getMethod();
            int x_dir = getX();
            int y_dir = getY();
            String inputkey =getKey();

            if (status == 1) {
                System.out.println( account+"님이 사망했습니다. 직업: "+ classname + ",방 번호 :"+sessionRoomID+", 군단장: "+bossInfo[0]);
                break;
            } else {
                if (method.equals("/wait")) {
                    OutLog(sessionRoomID, now, finalTime,success,classname,account, ipAddr,bossInfo[0], bossInfo[1], bossInfo[2], method, 0, 0, inputkey, status,producer);
                }else {
                    if (bossInfo[0].equals("Valtan")){
                        if (rand.nextDouble() > 0.99) {
                            status = 1;
                        }else if ((int)runTime_seconds > Integer.valueOf(bossInfo[2])){
                            success = 1;
                            OutLog(sessionRoomID, now, finalTime,success,classname,account, ipAddr,bossInfo[0], bossInfo[1], bossInfo[2], method, x_dir, y_dir, inputkey, status,producer);
                            break;
                        }
                    }else if (bossInfo[0].equals("Abrelshud")) {
                        if (rand.nextDouble() > 0.990) {
                            status = 1;
                        }else if ((int)runTime_seconds > Integer.valueOf(bossInfo[2])){
                            success = 1;
                            OutLog(sessionRoomID, now, finalTime,success,classname,account, ipAddr,bossInfo[0], bossInfo[1], bossInfo[2], method, x_dir, y_dir, inputkey, status,producer);
                            break;
                        }
                    }else if (bossInfo[0].equals("Kamen")) {
                        if (rand.nextDouble() > 0.50) {
                            status = 1;
                        }else if ((int)runTime_seconds > Integer.valueOf(bossInfo[2])){
                            success = 1;
                            OutLog(sessionRoomID, now, finalTime,success,classname,account, ipAddr,bossInfo[0], bossInfo[1], bossInfo[2], method, x_dir, y_dir, inputkey, status,producer);
                            break;
                        }
                    }else if (bossInfo[0].equals("Kouku-Saton")) {
                        if (rand.nextDouble() > 0.99) {
                            status = 1;
                        }else if ((int)runTime_seconds > Integer.valueOf(bossInfo[2])){
                            success = 1;
                            OutLog(sessionRoomID, now, finalTime,success,classname,account, ipAddr,bossInfo[0], bossInfo[1], bossInfo[2], method, x_dir, y_dir, inputkey, status,producer);
                            break;
                        }
                    }else if (bossInfo[0].equals("Illiakan")) {
                        if (rand.nextDouble() > 0.99) {
                            status = 1;
                        }else if ((int)runTime_seconds > Integer.valueOf(bossInfo[2])){
                            success = 1;
                            OutLog(sessionRoomID, now, finalTime,success,classname,account, ipAddr,bossInfo[0], bossInfo[1], bossInfo[2], method, x_dir, y_dir, inputkey, status,producer);
                            break;
                        }
                    }else if (bossInfo[0].equals("Biackiss")) {
                        if (rand.nextDouble() > 0.99) {
                            status = 1;
                        }else if ((int)runTime_seconds > Integer.valueOf(bossInfo[2])){
                            success = 1;
                            OutLog(sessionRoomID, now, finalTime,success,classname,account, ipAddr,bossInfo[0], bossInfo[1], bossInfo[2], method, x_dir, y_dir, inputkey, status,producer);
                            break;
                        }
                    }
                    OutLog(sessionRoomID, now, finalTime,success,classname,account, ipAddr,bossInfo[0], bossInfo[1], bossInfo[2], method, x_dir, y_dir, inputkey, status,producer);
                }
            }
        }
        System.out.println( account+"님의 작업이 끝났습니다. 직업: "+ classname + ",방 번호 :"+sessionRoomID+", 군단장: "+bossInfo[0]);
        this.latch.countDown();
    }

    private  void OutLog(String sessionRoomID, LocalDateTime startTime, String finaltime, Integer success, String classname, String account, String ipAddr, String bossName, String bossDiff, String bossEndTime, String method, int x_dir, int y_dir, String inputkey, int status, KafkaProducer<String, String> producer) {
        String log = String.format(
                "{" +
                        "\"sessionID\": \"%s\"," +
                        "\"startTime\": \"%s\"," +
                        "\"gametime\": \"%s\"," +
                        "\"success\": \"%s\", " +
                        "\"User\": {" +
                        "        \"class\": \"%s\"," +
                        "        \"account\": \"%s\"," +
                        "        \"ip\": \"%s\"" +
                        " }," +
                        "\"Boss\": {" +
                        "        \"name\": \"%s\"," +
                        "        \"item_level\": \"%s\"," +
                        "        \"endTime\": \"%s\"" +
                        " }," +
                        " \"method\": \"%s\"," +
                        " \"x\": \"%s\"," +
                        " \"y\": \"%s\"," +
                        " \"inpukey\": \"%s\"," +
                        " \"status\": \"%s\"" + "}",sessionRoomID,startTime,finaltime,success,classname,account,ipAddr,bossName,bossDiff,bossEndTime,method,x_dir,y_dir,inputkey,status);

        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(log);
            producer.send(new ProducerRecord<>(TOPIC_NAME, String.valueOf(jsonObject)));
            System.out.println(jsonObject);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isDuration(long startTime) {
        return System.currentTimeMillis() - startTime < durationSeconds * 1000L;
    }

    private String getMethod() {
        if (rand.nextDouble() > 0.85) {
            return "/wait";
        } else if (rand.nextDouble() > 0.90) {
            return "/chat";
        }
        else {
            return "/move";
        }
    }

    private int getX(){
        int x = 0;

        if (rand.nextDouble() > 0.99) {
            x = rand.nextInt(1250 - (-1250) +1) + (-1250);
            return x;
        } else {
            x = rand.nextInt(400 - (-400) +1) + (-400);
            return x;
        }
    }

    private int getY(){
        int y = 0;

        if (rand.nextDouble() > 0.99) {
            y = rand.nextInt(1250 - (-1250) +1) + (-1250);
            return y;
        } else {
            y = rand.nextInt(400 - (-400) +1) + (-400);
            return y;
        }
    }

    private String getKey() {
        if (rand.nextDouble() > 0.80) {
            String[] arrKey = new String[]{"alt", "tab", "alt+tab", "esc", "shift", "enter"};
            int num = rand.nextInt(arrKey.length);
            String key = arrKey[num];

            return key;
        } else {
            String[] arrKey = new String[]{"q", "w", "e", "r","a", "s", "d", "f", "space", "b"};
            int num = rand.nextInt(arrKey.length);
            String key = arrKey[num];

            return key;
        }
    }

}

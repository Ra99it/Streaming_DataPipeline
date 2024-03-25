package LostArkCommanderLogGenerator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

            if (method.equals("/wait")) {
                OutLog(sessionRoomID, now, finalTime,success,classname,account, ipAddr,bossInfo[0], bossInfo[1], bossInfo[2], method, 0, 0, inputkey, status);
            }else if (status == 1){
                break;
            } else {
                if (bossInfo[0].equals("Valtan")){
                    if (rand.nextDouble() > 0.99) {
                        status = 1;
                    }else if (String.valueOf(runTime_seconds).equals(bossInfo[2])){
                        success = 1;
                        break;
                    }
                }else if (bossInfo[0].equals("Abrelshud")) {
                    if (rand.nextDouble() > 0.98) {
                        status = 1;
                    }else if ((int)runTime_seconds > Integer.valueOf(bossInfo[2])){
                        success = 1;
                        break;
                    }
                }else if (bossInfo[0].equals("Kamen")) {
                    if (rand.nextDouble() > 0.95) {
                        status = 1;
                    }else if ((int)runTime_seconds > Integer.valueOf(bossInfo[2])){
                        success = 1;
                        break;
                    }
                }else if (bossInfo[0].equals("Kouku-Saton")) {
                    if (rand.nextDouble() > 0.99) {
                        status = 1;
                    }else if ((int)runTime_seconds > Integer.valueOf(bossInfo[2])){
                        success = 1;
                        break;
                    }
                }else if (bossInfo[0].equals("Illiakan")) {
                    if (rand.nextDouble() > 0.98) {
                        status = 1;
                    }else if ((int)runTime_seconds > Integer.valueOf(bossInfo[2])){
                        success = 1;
                        break;
                    }
                }else if (bossInfo[0].equals("Biackiss")) {
                    if (rand.nextDouble() > 0.99) {
                        status = 1;
                    }else if ((int)runTime_seconds > Integer.valueOf(bossInfo[2])){
                        success = 1;
                        break;
                    }
                }
                OutLog(sessionRoomID, now, finalTime,success,classname,account, ipAddr,bossInfo[0], bossInfo[1], bossInfo[2], method, x_dir, y_dir, inputkey, status);
            }
        }

        if (status == 1) {
            System.out.println( account+"님이 사망했습니다. 직업: "+ classname + ",방 번호 :"+sessionRoomID+", 군단장: "+bossInfo[0]);
        }
        System.out.println( account+"님의 작업이 끝났습니다. 직업: "+ classname + ",방 번호 :"+sessionRoomID+", 군단장: "+bossInfo[0]);
        this.latch.countDown();
    }

    private  void OutLog(String sessionRoomID, LocalDateTime startTime, String finaltime, Integer success, String classname, String account, String ipAddr, String bossName, String bossDiff, String bossEndTime, String method, int x_dir, int y_dir, String inputkey, int status) {
        String log = String.format("{\n" +
                "    \"sessionID\": \"%s\",\n" +
                "    \"startTime\": \"%s\",\n" +
                "    \"gametime\": \"%s\",\n" +
                "    \"success\": \"%s\", \n" +
                "    \"User\": {\n" +
                "        \"class\": \"%s\",\n" +
                "        \"account\": \"%s\",\n" +
                "        \"ip\": \"%s\"\n" +
                "    },\n" +
                "    \"Boss\": {\n" +
                "        \"name\": \"%s\",\n" +
                "        \"difficulty\": \"%s\",\n" +
                "        \"endTime\": \"%s\"\n" +
                "    },\n" +
                "    \"method\": \"%s\",\n" +
                "    \"x\": \"%s\",\n" +
                "    \"y\": \"%s\",\n" +
                "    \"inpukey\": \"%s\",\n" +
                "    \"status\": \"%s\"\n" +
                "}",sessionRoomID,startTime,finaltime,success,classname,account,ipAddr,bossName,bossDiff,bossEndTime,method,x_dir,y_dir,inputkey,status);

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
        if (rand.nextDouble() > 0.85) {
            return "/wait";
        }else {
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
        if (rand.nextDouble() > 0.50) {
            return 1;
        } else {
            return 0;
        }
    }
}
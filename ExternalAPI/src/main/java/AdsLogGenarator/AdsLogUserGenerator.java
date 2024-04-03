package AdsLogGenarator;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class AdsLogUserGenerator implements Runnable{
    private String adID;
    private String ad_name;
    private String ad_explain;
    private String ad_genre;
    private Integer viewing_time;
    private Integer click_count;
    private String ad_start_time;
    private String ad_end_time;
    private static Random rand;
    private CountDownLatch latch;
    private String account;
    private String gender;
    private Integer age;
    private Integer total_time;
    private boolean click;
    private boolean view = false;
    private int durationSeconds;

    private final long MINIMUM_SLEEP_TIME = 100;
    private final long MAXIMUM_SLEEP_TIME = 60 * 300;

    private final String TOPIC_NAME = "adlogs";

    private long click_time = 0;
    private Integer click_sum = 0;

    private long viewTime_start = 0;
    private long view_sum = 0;
    public AdsLogUserGenerator(CountDownLatch latch, Integer durationSeconds, String adID, String ad_name, String ad_explain, String ad_genre, Integer viewing_time, Integer click_count, String ad_start_time, String ad_end_time, String account, String gender, Integer age, Integer total_time, boolean click){
        this.latch = latch;
        this.durationSeconds = durationSeconds;
        this.adID = adID;
        this.ad_name = ad_name;
        this.ad_explain = ad_explain;
        this.ad_genre = ad_genre;
        this.viewing_time = viewing_time;
        this.click_count = click_count;
        this.ad_start_time = ad_start_time;
        this.ad_end_time = ad_end_time;
        this.account = account;
        this.gender = gender;
        this.age = age;
        this.total_time = total_time;
        this.click = click;
        this.rand = new Random();
    }
    @Override
    public void run() {

        Properties props = new Properties();
        //props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-cluster-01:9092,kafka-cluster-02:9092,kafka-cluster-03:9092");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "spark-worker-01:9092,spark-worker-02:9092,spark-worker-03:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "LostArkCommanderLogGenerator");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        long startTime = System.currentTimeMillis();

        while(isDuration(startTime)) {
            long sleepTime = MINIMUM_SLEEP_TIME + Double.valueOf(rand.nextDouble() * (MAXIMUM_SLEEP_TIME)).longValue();

            try{
                Thread.sleep(sleepTime);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneId.of("UTC"));

            if (click) {
                long waitTime = getWaitTime(click_time);
                if (waitTime > 60) {
                    click = false;
                    waitTime = 0;
                }
            } else {
                if (gender.equals("male")) {
                    if (rand.nextDouble() > 0.99) {
                        click = true;
                        click_sum += 1;
                        click_time = System.currentTimeMillis();
                        OutLog(adID, ad_name, ad_explain, ad_genre, view_sum, click_sum, ad_start_time, ad_end_time, account, gender, age, total_time, click, view, offsetDateTime,producer);
                    }
                }else if (gender.equals("female")) {
                    if (rand.nextDouble() > 0.99) {
                        click = true;
                        click_sum += 1;
                        click_time = System.currentTimeMillis();
                        OutLog(adID, ad_name, ad_explain, ad_genre, view_sum, click_sum, ad_start_time, ad_end_time, account, gender, age, total_time, click, view, offsetDateTime,producer);
                    }
                }
            }

            if (view) {
                long viewTime = getViewTime(viewTime_start);
                if (rand.nextDouble() > 0.40) {
                    view = false;
                    total_time = (int) viewTime;
                    view_sum += total_time;
                    OutLog(adID, ad_name, ad_explain, ad_genre, view_sum, click_sum, ad_start_time, ad_end_time, account, gender, age, total_time, click, view, offsetDateTime, producer);
                }
            } else {
                if (gender.equals("male")) {
                    if (rand.nextDouble() > 0.95) {
                        view = true;
                        total_time = 0;
                        viewTime_start = System.currentTimeMillis();
                        OutLog(adID, ad_name, ad_explain, ad_genre, view_sum, click_sum, ad_start_time, ad_end_time, account, gender, age, total_time, click, view, offsetDateTime, producer);
                    }
                }else if (gender.equals("female")) {
                    if (rand.nextDouble() > 0.95) {
                        view = true;
                        total_time = 0;
                        viewTime_start = System.currentTimeMillis();
                        OutLog(adID, ad_name, ad_explain, ad_genre, view_sum, click_sum, ad_start_time, ad_end_time, account, gender, age, total_time, click, view, offsetDateTime, producer);
                    }
                }
            }
        }
        this.latch.countDown();
    }

    private  void OutLog(String adID, String ad_name, String ad_explain, String ad_genre, long viewing_time, Integer click_count, String ad_start_time, String ad_end_time, String account, String gender, Integer age, Integer total_time, boolean click, boolean view, OffsetDateTime offsetDateTime,KafkaProducer<String, String> producer) {
        String log = String.format(
                "{\n" +
                        "\"Ad\": {" +
                        "    \"ad_id\": \"%s\"," +
                        "    \"ad_name\": \"%s\"," +
                        "    \"ad_explain\": \"%s\"," +
                        "    \"genre\": \"%s\"," +
                        "    \"viewing_time\": \"%s\"," +
                        "    \"click_count\": \"%s\"," +
                        "    \"start_time\": \"%s\"," +
                        "    \"end_time\": \"%s\"" +
                        "  }," +
                        "  \"User\": {" +
                        "    \"account\": \"%s\"," +
                        "    \"gender\": \"%s\"," +
                        "    \"age\": \"%s\"," +
                        "    \"click\": \"%s\"," +
                        "    \"view\": \"%s\"," +
                        "    \"total_time\": \"%s\"," +
                        "    \"offset_time\": \"%s\"" +
                        "}" + "}",adID, ad_name, ad_explain, ad_genre, viewing_time, click_count, ad_start_time, ad_end_time, account, gender, age, click, view,total_time,offsetDateTime
        );

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

    private static long getWaitTime(long click_time) {
        long click_end_time = System.currentTimeMillis();

        return (click_end_time-click_time) / 1000;
    }

    private static long getViewTime(long view_time) {
        long view_end_time = System.currentTimeMillis();

        return (view_end_time-view_time) / 1000;
    }

}

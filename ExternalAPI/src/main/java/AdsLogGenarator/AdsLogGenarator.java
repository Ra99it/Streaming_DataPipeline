package AdsLogGenarator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class AdsLogGenarator implements Runnable{
    private String adID;
    private String ad_name;
    private String ad_explain;
    private String ad_genre;
    private Integer viewing_time;
    private Integer click_count;
    private String ad_start_time;
    private String ad_end_time;
    private static Random rand;
    static int userNum = 1;
    static int durationSeconds = 600;
    static Set<String> accSet = new HashSet<>();

    public AdsLogGenarator(String adID, String ad_name, String ad_explain, String ad_genre, Integer viewing_time, Integer click_count, String ad_start_time, String ad_end_time) {
        this.adID = adID;
        this.ad_name = ad_name;
        this.ad_explain = ad_explain;
        this.ad_genre = ad_genre;
        this.viewing_time = viewing_time;
        this.click_count = click_count;
        this.ad_start_time = ad_start_time;
        this.ad_end_time = ad_end_time;
        this.rand = new Random();
    }

    CountDownLatch latch = new CountDownLatch(userNum);
    ExecutorService executor = Executors.newFixedThreadPool(userNum);

    @Override
    public void run() {
        IntStream.range(0,userNum).forEach(j -> {
            String account = getAccount();
            String gender = getGender();
            Integer age = getAge();
            Integer total_time = 0;
            boolean click = false;

            executor.execute(new AdsLogUserGenerator(latch, durationSeconds,adID, ad_name, ad_explain, ad_genre, viewing_time, click_count, ad_start_time, ad_end_time, account, gender, age, total_time, click));
        });

        executor.shutdown();

        try {
            latch.await();
        }catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    private static String getAccount() {
        while (true){
            String account = "testAccount_" + rand.nextInt(100);

            if (!accSet.contains(account)){
                accSet.add(account);

                return account;
            }
        }
    }

    private static String getGender() {
        if (rand.nextDouble() > 0.5) {
            return "male";
        } else {
            return "female";
        }
    }

    private static Integer getAge() {
        int age = rand.nextInt(100);

        return age;
    }
}

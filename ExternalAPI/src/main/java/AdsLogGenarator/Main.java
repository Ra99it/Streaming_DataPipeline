package AdsLogGenarator;

import LostArkCommanderLogGenerator.LoasArkRoomGenerator;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {
    static int adsNum = 4;
    static Set<String> adSet = new HashSet<>();
    static Random rand = new Random();
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(adsNum);
        ExecutorService executor = Executors.newFixedThreadPool(adsNum);

        IntStream.range(0,adsNum).forEach(j -> {
            String adID = getAdID();
            String[] apinfo = getAdInfo();

            String ad_name = apinfo[0];
            String ad_explain = apinfo[1];
            String ad_genre = apinfo[2];
            String ad_start_time = apinfo[3];
            String ad_end_time = apinfo[4];

            Integer viewing_time = 0;
            Integer click_count = 0;

            executor.execute(new AdsLogGenarator(adID, ad_name, ad_explain, ad_genre, viewing_time, click_count, ad_start_time, ad_end_time));
        });

        executor.shutdown();

        try {
            latch.await();
        }catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    public static String getAdID() {
        String getAdID = String.valueOf(UUID.randomUUID());

        return getAdID;
    }


    public static String[] getAdInfo() {

        while (true) {
            String[] ad = new String[5];
            int num = rand.nextInt(adsNum);

            switch (num) {
                case 0 :
                    ad[0] = "야놀자";
                    ad[1] = "숙박과 관련된 광고입니다.";
                    ad[2] = "lodgment,event";
                    ad[3] = "2024-03-31";
                    ad[4] = "2024-04-15";
                    break;
                case 1 :
                    ad[0] = "리그오브레전드";
                    ad[1] = "리그오브레전드와 관련된 광고입니다.";
                    ad[2] = "game";
                    ad[3] = "2024-03-31";
                    ad[4] = "2024-04-15";
                    break;
                case 2 :
                    ad[0] = "유튜브";
                    ad[1] = "유튜브 광고 입니다.";
                    ad[2] = "youtube";
                    ad[3] = "2024-03-31";
                    ad[4] = "2024-04-15";
                    break;
                default:
                    ad[0] = "로스트아크";
                    ad[1] = "RPG와 관련된 광고입니다.";
                    ad[2] = "game";
                    ad[3] = "2024-03-31";
                    ad[4] = "2024-04-15";
                    break;
            }

            if (!adSet.contains(ad[0])) {
                adSet.add(ad[0]);

                return ad;
            }
        }
    }
}

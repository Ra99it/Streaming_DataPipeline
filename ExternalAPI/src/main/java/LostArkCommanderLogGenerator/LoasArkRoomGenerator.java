package LostArkCommanderLogGenerator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class LoasArkRoomGenerator implements Runnable{
    private final String sessionRoomID;

    static int userNum = 8;
    static Random rand = new Random();
    static Set<String> ipSet = new HashSet<>();
    static Set<String> accSet = new HashSet<>();
    static int durationSeconds = 1300;

    public LoasArkRoomGenerator(String sessionRoomID) {
        this.sessionRoomID = sessionRoomID;
    }
    CountDownLatch latch = new CountDownLatch(userNum);
    ExecutorService executor = Executors.newFixedThreadPool(userNum);
    @Override
    public void run() {
        String[] bossInfo = getBossInfo();
        IntStream.range(0,userNum).forEach(j -> {
                //String[] bossInfo = getBossInfo();
                String ipAddr = getIpAddr();
                String account = getAccount();
                String classname = getClassName();
                Integer success = 0;

                executor.execute(new LostArkCommanerLogGenerator(latch, ipAddr, account, classname,UUID.randomUUID().toString(), durationSeconds, sessionRoomID, bossInfo, success));
        });

        executor.shutdown();

        try {
            latch.await();
        }catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    private static String getIpAddr() {
        while (true){
            String ipAddr = "192.168.0." + rand.nextInt(256);

            if (!ipSet.contains(ipAddr)){
                ipSet.add(ipAddr);

                return ipAddr;
            }
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
    private static String getClassName() {
        String[] classArray = new String[] {"Berserker", "Destroyer", "Warlord", "Holyknight", "Striker", "Breaker", "Artist", "Aeromancer"
                , "Blade", "Arcana", "Bard", "Scouter", "DevilHunter", "Infighter", "Slayer", "Battle Master", "Soul Master", "Lance Master", "Blaster",
                "Hawk Eye", "Gunslinger", "Arcana", "Summoner", "Sorceress", "Demonic", "Reaper", "Souleater"};

        return classArray[rand.nextInt(classArray.length)];
    }

    public static String[] getBossInfo() {
        Integer num = rand.nextInt(6);
        String[] bossInfo;
        switch (num) {
            case 0:
                bossInfo = new String[]{"Valtan", "1445", "600"};
                break;
            case 1:
                bossInfo = new String[]{"Abrelshud", "1560", "900"};
                break;
            case 2:
                bossInfo = new String[]{"Kamen", "1630", "1200"};
                break;
            case 3:
                bossInfo = new String[]{"Kouku-Saton", "1475", "600"};
                break;
            case 4:
                bossInfo = new String[]{"Illiakan", "1600", "600"};
                break;
            case 5:
                bossInfo = new String[]{"Biackiss", "1460", "600"};
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + num);
        }
        return bossInfo;
    }
}

package DataGenerator;

import LogGenerator.LogGenerator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {
    static int userNum = 6;
    static int durationSeconds = 1200;

    static int championNum = userNum;
    static Set<String> ipSet = new HashSet<>();
    static Set<String> accSet = new HashSet<>();
    static Set<Integer> championSet = new HashSet<>();
    static Random rand = new Random();

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(userNum);
        ExecutorService executor = Executors.newFixedThreadPool(userNum);
        IntStream.range(0, userNum).forEach(i -> {
            String ipAddr = getIpAddr();
            String account = getAccount();
            String champion = getChampions();
            executor.execute(new DataGenerator(latch, ipAddr, account,UUID.randomUUID().toString(), champion,durationSeconds));
        });
    }

    private static String getChampions() {
        int championStatus = rand.nextInt(championNum);
        String championName = "";
        if (!championSet.contains(championStatus)){
            championSet.add(championStatus);

            if (championStatus == 0) {
                championName = "azir";
            } else if (championStatus == 1) {
                championName = "viktor";
            } else if (championStatus == 2) {
                championName = "orianna";
            } else if (championStatus == 3) {
                championName = "vex";
            } else if (championStatus == 4) {
                championName = "ryze";
            } else {
                championName = "zilean";
            }
            return championName;
        } else {
            String re = getChampions();
            return re;
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
    }



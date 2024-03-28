package LostArkCommanderLogGenerator;

import lodgmentDataGenerator.LodgmentDataGenerator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {
    static int roomNum = 5;
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(roomNum);
        ExecutorService executor = Executors.newFixedThreadPool(roomNum);

        IntStream.range(0,roomNum).forEach(j -> {
            String sessionRoomID = getSessionRoomID();
            executor.execute(new LoasArkRoomGenerator(sessionRoomID));
        });

        executor.shutdown();

        try {
            latch.await();
        }catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    public static String getSessionRoomID() {
        String sessionRoomID = String.valueOf(UUID.randomUUID());

        return sessionRoomID;
    }
}



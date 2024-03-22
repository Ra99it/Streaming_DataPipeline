package ImageGenerator;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.UUID;

public class main {
   static int userNum = 5;
    static int durationSeconds = 300;
    static Random rand = new Random();
    static Set<String> fileSet = new HashSet<>();
    static String filePath = "D:\\포트폴리오\\Data\\Image\\";

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(userNum);
        ExecutorService executor = Executors.newFixedThreadPool(userNum);
        IntStream.range(0, userNum).forEach(i -> {
            String file = getFilePath();
            executor.execute(new ImageGenerator(latch, file, UUID.randomUUID().toString(),durationSeconds));
        });

        executor.shutdown();

        try {
            latch.await();
        }catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    private static String getFilePath() {
        while (true) {
            String fileFilePath = filePath + rand.nextInt(20) + ".jpg";
            if(!fileSet.contains(fileFilePath)){
                fileSet.add(fileFilePath);
                return fileFilePath;
            }
        }
    }
}

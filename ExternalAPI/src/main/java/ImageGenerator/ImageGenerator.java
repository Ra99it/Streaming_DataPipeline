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
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class ImageGenerator implements Runnable{
    private CountDownLatch latch;
    private String file;
    private String sessionID;
    private int durationSeconds;
    private Random rand;

    private final long MINIMUM_SLEEP_TIME = 500;
    private final long MAXIMUM_SLEEP_TIME = 60 * 1000;

    private final String TOPIC_NAME = "images";

    public ImageGenerator(CountDownLatch latch, String file, String sessionID, int durationSeconds) {
        this.latch = latch;
        this.file = file;
        this.sessionID = sessionID;
        this.durationSeconds = durationSeconds;
        this.rand = new Random();
    }

    @Override
    public void run() {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-cluster-01:9092,kafka-cluster-02:9092,kafka-cluster-03:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "ImageGenerator");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        Producer<String, byte[]> producer = new KafkaProducer<>(props);

        long startTime = System.currentTimeMillis();

        while (isDuration(startTime)){
            long sleepTime = MINIMUM_SLEEP_TIME + Double.valueOf(rand.nextDouble() * (MAXIMUM_SLEEP_TIME)).longValue();

            try{
                Thread.sleep(sleepTime);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            try {
                byte[] imageByte = getImageByte(file);

                ProducerRecord<String, byte[]> record = new ProducerRecord<>(TOPIC_NAME, imageByte);
                System.out.println("file: "+ file + ", byte: " + imageByte);

                producer.send(record);
                producer.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        producer.close();
        System.out.println("Stopping....");
        this.latch.countDown();
    }

    private boolean isDuration(long startTime) {
        return System.currentTimeMillis() - startTime < durationSeconds * 1000L;
    }

    private byte[] getImageByte(String file) throws IOException {
        byte[] imageInByte;

        BufferedImage originalImage = ImageIO.read(new File(file));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", baos);
        baos.flush();

        imageInByte = baos.toByteArray();
        ByteBuffer imageInByteBuffer = ByteBuffer.wrap(imageInByte);
        byte[] image_byte = imageInByteBuffer.array();
        baos.close();

        return image_byte;
    }
}


    /*public static void main(String[] args) throws IOException {
        String filePath = "D:\\포트폴리오\\Data\\Image\\";
        String fileName = "";

        File rw = new File(filePath);
        File[] fileList = rw.listFiles();

        for(File file : fileList) {
            if(file.isFile()) {
                fileName = file.getName();
                System.out.println(filePath+fileName);
            }
        }

        System.out.println("사진의 경로를 입력 해 주세요.");
        while (true) {
            Scanner scanner = new Scanner(System.in);

            String inputPath = scanner.nextLine();

            byte[] imageInByte;

            BufferedImage originalImage = ImageIO.read(new File(inputPath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();

            imageInByte = baos.toByteArray();
            ByteBuffer imageInByteBuffer = ByteBuffer.wrap(imageInByte);
            byte[] image_byte = imageInByteBuffer.array();

            System.out.println(imageInByteBuffer);
            baos.close();

            //*******Producer******************
            Properties props = new Properties();

            String BOOTSTRAP_SERVER = "kafka-cluster-01:9092,kafka-cluster-02:9092,kafka-cluster-03:9092";
            String TOPIC_NAME = "images";

            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

            Producer<String, byte[]> producer = new KafkaProducer<>(props);

            ProducerRecord<String, byte[]> record = new ProducerRecord<>(TOPIC_NAME, image_byte);
            producer.send(record);

            producer.flush();

            producer.close();
            //*********************************

            }

        }*/


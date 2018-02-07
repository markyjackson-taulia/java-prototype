package heptio.javaPrototype;

import heptio.util.HumanReadableBytes;
import heptio.util.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@EnableAutoConfiguration
public class NoisyApplicationController {

    private static final long minSleepSeconds = 3L;
    private static final long maxSleepSeconds = 20L;
    private static final Logger logger = LoggerFactory.getLogger(NoisyApplicationController.class);
    private static NoisyApplicationController ourInstance = new NoisyApplicationController();

    public static NoisyApplicationController getInstance() {
        return ourInstance;
    }

    private NoisyApplicationController() {
        // Constructor
    }

    public void run() throws Exception {
        while (true) {
            this.bloat(100, 1024 * 1024);
            long totalMemory = Runtime.getRuntime().maxMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            logger.info("Total memory      : " + String.valueOf(HumanReadableBytes.humanReadableByteCount(totalMemory, false)));
            logger.info("Free memory       : " + String.valueOf(HumanReadableBytes.humanReadableByteCount(freeMemory, false)));
            logger.info("Number of threads : " + Thread.activeCount());
            this.sleep();
        }
    }

    protected HashMap cache = new HashMap();

    /**
     * cache is a simple method that will add a new item to a memory cache
     *
     * @param numberOfBytesToCache
     */
    protected void cache(int numberOfBytesToCache) {
        String uniqueID = UUID.randomUUID().toString();
        String cacheMessage = RandomStringGenerator.generateString(numberOfBytesToCache);
        cache.put(uniqueID,cacheMessage);
       // logger.info("Caching ID: " + uniqueID);
    }

    /**
     * bloat is an asynchronous method that will spawn child threads each consuming memory.
     *
     * @param numberOfThreads
     * @param numberOfBytesPerThread
     */
    protected void bloat(int numberOfThreads, int numberOfBytesPerThread) {
        for(int i = 0; i < numberOfThreads; i++ ) {
            /*
             * By caching BEFORE we start a new thread, each thread will implicitly take up as much memory
             * as the program was before, as well as the new memory for this thread. The problem will grow
             * linearly as we introduce more threads. O(N)
             */
            this.cache(numberOfBytesPerThread);
            Runnable task = () -> {
                this.cache(numberOfBytesPerThread);
            };
            Thread thread = new Thread(task);
            thread.start();
           // logger.info("Starting thread: " + String.valueOf(i));
        }
    }

    protected void sleep() {
        long generatedLong = minSleepSeconds + (long) (Math.random() * (minSleepSeconds - maxSleepSeconds));
        try {
            TimeUnit.SECONDS.sleep(generatedLong);
        } catch (InterruptedException e) {
            logger.error("Unable to sleep thread: " + e.getMessage());
        }
    }
}
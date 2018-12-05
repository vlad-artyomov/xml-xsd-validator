package com.artyomov.validator;

import com.artyomov.validator.service.XmlValidateService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
        "reference.xsd.path=src/test/resources/sample.xsd",
})
public class XmlXsdValidatorAppTests {

    private static final int VALIDATE_RUNS = 1000;

    private static final int THREADS_NUMBER = 4;

    @Autowired
    private XmlValidateService xmlValidateService;

    @Autowired
    private Logger logger;

    @Ignore
    @Test
    public void shouldValidateXml() throws IOException, ExecutionException, InterruptedException {
        // given
        final String validXml = readFile("src/test/resources/sample.xml");
        ExecutorService service = Executors.newFixedThreadPool(THREADS_NUMBER);
        ArrayList<Future<Long>> futures = new ArrayList<>(THREADS_NUMBER);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean running = new AtomicBoolean();
        AtomicInteger overlaps = new AtomicInteger();

        // when
        for (int i = 0; i < THREADS_NUMBER; i++) {
            futures.add(service.submit(() -> {
                try {
                    latch.await();
                    if (running.get()) {
                        overlaps.incrementAndGet();
                    }
                    running.set(true);
                    long averageTime = 0;
                    for (int j = 0; j < VALIDATE_RUNS; j++) {
                        averageTime += checkValidationTime(validXml);
                    }
                    running.set(false);
                    return averageTime;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return 0L;
                }
            }));
        }
        latch.countDown();

        // find intersection in generated sets
        Long averageTime = futures.get(0).get();
        for (int i = 1; i < futures.size(); i++) {
            averageTime += futures.get(i).get();
        }

        // then
        logger.info("Average XML validation time is {} ms for {} runs in {} parallel threads",
                (double) averageTime / (VALIDATE_RUNS * THREADS_NUMBER), VALIDATE_RUNS, THREADS_NUMBER);
    }

    private long checkValidationTime(String xmlToValidate) {
        long startTime = System.currentTimeMillis();
        xmlValidateService.validateXml(xmlToValidate).subscribe();
        long totalTime = System.currentTimeMillis() - startTime;
        logger.info("XML validation completed in {} ms", totalTime);
        return totalTime;
    }

    private String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.defaultCharset());
    }
}
